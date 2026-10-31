const fs = require('node:fs');
const path = require('node:path');
const folders = [];
function folder(name) { const f = { name, item: [] }; folders.push(f); return f.item; }
function request(items, name, method, route, code = 200, body, message, extra = '') {
  const tests = [`pm.test('HTTP ${code}', () => pm.response.to.have.status(${code}));`];
  if (message) tests.push(`pm.test('Exact message', () => pm.expect(pm.response.text()).to.eql(${JSON.stringify(message)}));`);
  if (extra) tests.push(extra);
  const req = { method, header: [], url: '{{baseUrl}}/api/' + route };
  if (body) { req.header.push({ key: 'Content-Type', value: 'application/json' }); req.body = { mode: 'raw', raw: JSON.stringify(body, null, 2), options: { raw: { language: 'json' } } }; }
  items.push({ name, request: req, event: [{ listen: 'test', script: { type: 'text/javascript', exec: tests } }] });
}
function capture(items, route, variable, predicate) {
  request(items, `Capture ${variable}`, 'GET', route + '/all', 200, null, null,
    `const row = pm.response.json().find(x => ${predicate}); pm.test('Seed record exists', () => pm.expect(row).to.exist); if (row) pm.collectionVariables.set('${variable}', row.id);`);
}
const seed = folder('Step 0 - seed via endpoints (fresh database; read README date note)');
['Maize', 'Beans', 'Irish Potatoes', 'Cassava'].forEach((name, i) => {
  request(seed, `A2 seed crop ${name}`, 'POST', 'crop-types/save', 201, { name }, 'Crop type saved successfully');
  capture(seed, 'crop-types', `crop${i + 1}`, `x.name === '${name}'`);
});
for (let i = 1; i <= 4; i++) {
  request(seed, `A2 seed warehouse ${i}`, 'POST', 'warehouses/save', 201, { warehouseNumber: i, name: `Warehouse ${i}` }, 'Warehouse saved successfully');
  capture(seed, 'warehouses', `warehouse${i}`, `x.warehouseNumber === ${i}`);
}
for (let i = 1; i <= 3; i++) {
  request(seed, `A2 seed agronomist ${i}`, 'POST', 'agronomists/save', 201, { firstName: `Agronomist${i}`, lastName: 'Demo', phone: `078000000${i}`, warehouse: { id: `{{warehouse${i}}}` }, cropTypes: (i === 1 ? [1, 2] : [i]).map(n => ({ id: `{{crop${n}}}` })) }, 'Agronomist saved successfully');
  capture(seed, 'agronomists', `agronomist${i}`, `x.firstName === 'Agronomist${i}'`);
}
for (let i = 1; i <= 5; i++) {
  request(seed, `A2 seed farmer ${i}`, 'POST', 'farmers/save', 201, { firstName: ['Alice', 'Eric', 'Jean', 'Marie', 'Paul'][i - 1], lastName: i <= 2 ? 'Niyonzima' : `Farmer${i}`, farmerCode: i === 1 ? 'FRM-0345' : `FRM-000${i}`, phone: `079000000${i}`, district: 'Gasabo' }, 'Farmer saved successfully');
  capture(seed, 'farmers', `farmer${i}`, `x.farmerCode === '${i === 1 ? 'FRM-0345' : `FRM-000${i}`}'`);
}
const forecasts = [
  ['2026-10-20',1,1,'WITHDRAWN'], ['2026-10-20',1,1,'DELIVERED'],
  ['2026-10-21',2,1,'DELIVERED'], ['2026-10-22',3,1,'VERIFIED'],
  ['2026-10-23',1,2,'SUBMITTED'], ['2026-11-01',2,2,'SUBMITTED'],
  ['2026-11-02',3,3,'VERIFIED'], ['2026-11-03',1,4,'WITHDRAWN'],
  ['2026-11-04',2,5,'SUBMITTED'], ['2026-11-05',3,5,'SUBMITTED']
];
forecasts.forEach(([date,a,f,status], index) => {
  const i = index + 1;
  request(seed, `A2 seed forecast ${i}`, 'POST', 'harvest-forecasts/save', 201, { forecastDate: date, cropName: 'Maize', expectedKg: 1000 + i, farmer: { id: `{{farmer${f}}}` }, agronomist: { id: `{{agronomist${a}}}` } }, 'Harvest forecast saved successfully');
  capture(seed, 'harvest-forecasts', `forecast${i}`, `x.expectedKg === ${1000+i}`);
  if (status !== 'SUBMITTED') {
    const stages = status === 'DELIVERED' ? ['VERIFIED','DELIVERED'] : [status];
    stages.forEach(s => request(seed, `D2 seed forecast ${i} -> ${s}`, 'PATCH', `harvest-forecasts/{{forecast${i}}}/status?status=${s}`, 200, null, 'Harvest forecast status updated successfully'));
  }
});
const main = folder('Requirements - run after seed, before mutation checks');
request(main, 'A1 - inspect JSON (verify SQL schema separately)', 'GET', 'crop-types/all', 200, null, null, "pm.test('No inverse agronomist list', () => pm.response.json().forEach(x => pm.expect(x).not.to.have.property('agronomists')));");
request(main, 'A2 - forecasts', 'GET', 'harvest-forecasts/all');
request(main, 'B1', 'GET', 'farmers/by-last-name?lastName=niyonzima', 200, null, null, "const rows = pm.response.json(); pm.test('Case insensitive and ordered', () => { pm.expect(rows.length).to.be.at.least(2); rows.forEach(x => pm.expect(x.lastName.toLowerCase()).to.eql('niyonzima')); pm.expect(rows.map(x=>x.firstName)).to.eql(rows.map(x=>x.firstName).sort()); });");
request(main, 'B2', 'GET', 'harvest-forecasts/by-status?status=SUBMITTED', 200, null, null, "const rows = pm.response.json(); pm.test('Status and date order', () => { rows.forEach(x=>pm.expect(x.status).to.eql('SUBMITTED')); pm.expect(rows.map(x=>x.forecastDate)).to.eql(rows.map(x=>x.forecastDate).sort()); });");
request(main, 'B3', 'GET', 'harvest-forecasts/between?start=2026-10-01&end=2026-10-31');
request(main, 'C1', 'GET', 'agronomists/by-crop-type?name=maize');
request(main, 'C2', 'GET', 'warehouses/free', 200, null, null, "pm.test('Warehouse 4 is free', () => pm.expect(pm.response.json().map(x=>x.warehouseNumber)).to.include(4));");
request(main, 'C3', 'GET', 'crop-types/unused', 200, null, null, "pm.test('Cassava is unused', () => pm.expect(pm.response.json().map(x=>x.name)).to.include('Cassava'));");
request(main, 'C4', 'GET', 'farmers/of-agronomist/{{agronomist1}}', 200, null, null, "const ids = pm.response.json().map(x=>x.id); pm.test('Distinct farmers', () => pm.expect(new Set(ids).size).to.eql(ids.length));");
request(main, 'E1', 'POST', 'weighing-tickets/save?harvestForecastId={{forecast2}}', 201, { produceName:'Maize grade A', remarks:'Dry and clean', weightKg:850 }, 'Weighing ticket saved successfully');
capture(main, 'weighing-tickets', 'ticket1', "x.harvestForecast.id === pm.collectionVariables.get('forecast2')");
request(main, 'E2', 'GET', 'weighing-tickets/farmer/FRM-0345');
request(main, 'F1', 'GET', 'harvest-forecasts/stats/by-status');
request(main, 'Bonus dashboard', 'GET', 'dashboard/summary', 200, null, null, "pm.test('Summary keys', () => pm.expect(Object.keys(pm.response.json())).to.eql(['farmers','agronomists','freeWarehouses','forecastsToday','submitted','weighingTickets']));");
request(main, 'Bonus pagination', 'GET', 'harvest-forecasts/page?page=0&size=5&sort=forecastDate,desc', 200, null, null, "pm.test('Page metadata', () => { const p = pm.response.json(); pm.expect(p.totalElements).to.be.at.least(10); pm.expect(p.totalPages).to.be.at.least(2); pm.expect(p.content.length).to.eql(5); });");
const checks = folder('Business rules and failure cases (mutates seed)');
const missing = '{{missingId}}';
request(checks, 'A2 - unknown farmer', 'GET', `farmers/${missing}`, 404, null, 'The farmer with that id does not exist');
request(checks, 'C4 - unknown agronomist', 'GET', `farmers/of-agronomist/${missing}`, 404, null, 'The agronomist with that id does not exist');
request(checks, 'D1 - past date', 'POST', 'harvest-forecasts/save', 400, { forecastDate:'2000-01-01' }, 'Harvest forecast date cannot be in the past');
request(checks, 'D1 - duplicate visit', 'POST', 'harvest-forecasts/save', 409, { forecastDate:'{{futureDate}}', agronomist:{ id:'{{agronomist1}}' }, farmer:{id:'{{farmer1}}'}, cropName:'Maize', expectedKg:1200 }, 'Agronomist already has a forecast visit on that date');
checks.splice(checks.length-1, 0, {name:'D1 - starts SUBMITTED regardless of body', request:{method:'POST',header:[{key:'Content-Type',value:'application/json'}],url:'{{baseUrl}}/api/harvest-forecasts/save',body:{mode:'raw',raw:JSON.stringify({forecastDate:'{{futureDate}}',status:'DELIVERED',agronomist:{id:'{{agronomist1}}'},farmer:{id:'{{farmer1}}'},cropName:'Maize',expectedKg:98765})}},event:[{listen:'prerequest',script:{exec:["const d = new Date(); d.setDate(d.getDate()+30); pm.collectionVariables.set('futureDate',d.getFullYear()+'-'+String(d.getMonth()+1).padStart(2,'0')+'-'+String(d.getDate()).padStart(2,'0'));" ]}},{listen:'test',script:{exec:["pm.test('Created',()=>pm.response.to.have.status(201));"]}}]});
request(checks, 'D1 - verify forced initial status', 'GET', 'harvest-forecasts/all', 200, null, null, "pm.test('Forced SUBMITTED',()=>pm.expect(pm.response.json().find(x=>x.expectedKg===98765).status).to.eql('SUBMITTED'));");
request(checks, 'D2 - final status blocked', 'PATCH', 'harvest-forecasts/{{forecast2}}/status?status=SUBMITTED',400,null,'Cannot change status from DELIVERED to SUBMITTED');
request(checks, 'D2 - verify submitted forecast', 'PATCH', 'harvest-forecasts/{{forecast5}}/status?status=VERIFIED',200,null,'Harvest forecast status updated successfully');
request(checks, 'D3 - add crop type', 'PUT', 'agronomists/{{agronomist2}}/crop-types/{{crop1}}',200,null,'Crop type added to the agronomist');
request(checks, 'D3 - duplicate crop type', 'PUT', 'agronomists/{{agronomist2}}/crop-types/{{crop1}}',409,null,'The agronomist already has that crop type');
request(checks, 'D4 - taken warehouse', 'PATCH', 'agronomists/{{agronomist1}}/warehouse?warehouseNumber=2',409,null,'That warehouse is taken by another agronomist');
request(checks, 'D4 - move warehouse', 'PATCH', 'agronomists/{{agronomist1}}/warehouse?warehouseNumber=4',200,null,'Warehouse changed successfully');
request(checks, 'D4 - old warehouse is free', 'GET', 'warehouses/free',200,null,null,"pm.test('Old warehouse free',()=>pm.expect(pm.response.json().map(x=>x.warehouseNumber)).to.include(1));");
request(checks, 'E1 - unknown forecast', 'POST', `weighing-tickets/save?harvestForecastId=${missing}`,404,{produceName:'Maize',weightKg:1},'The harvest forecast with that id does not exist');
request(checks, 'E1 - forecast not delivered', 'POST', 'weighing-tickets/save?harvestForecastId={{forecast5}}',400,{produceName:'Maize',weightKg:1},'Weighing tickets are only for DELIVERED harvest forecasts');
request(checks, 'E3 - protect forecast', 'DELETE', 'harvest-forecasts/delete/{{forecast2}}',409,null,'That harvest forecast has weighing tickets, delete them first');
request(checks, 'E3 - delete ticket', 'DELETE', 'weighing-tickets/delete/{{ticket1}}',200,null,'Weighing ticket deleted successfully');
request(checks, 'E3 - unknown ticket', 'DELETE', `weighing-tickets/delete/${missing}`,404,null,'The weighing ticket with that id does not exist');
request(checks, 'F2', 'PATCH', 'harvest-forecasts/cancel-day?agronomistId={{agronomist1}}&date=2026-10-20',200,null,'1 harvest forecasts cancelled');
request(checks, 'F2 - delivered preserved', 'GET', 'harvest-forecasts/{{forecast2}}',200,null,null,"pm.test('Delivered preserved',()=>pm.expect(pm.response.json().status).to.eql('DELIVERED'));");
const crud = folder('A2 - CRUD reference (run individually; seed relationships may prevent deletion)');
for (const [route,id,body,label] of [
  ['crop-types','crop1',{name:'Maize'},'crop type'], ['warehouses','warehouse1',{warehouseNumber:1,name:'Warehouse 1'},'warehouse'],
  ['agronomists','agronomist1',{firstName:'Agronomist1',lastName:'Demo',phone:'0780000001',warehouse:{id:'{{warehouse4}}'},cropTypes:[{id:'{{crop1}}'},{id:'{{crop2}}'}]},'agronomist'],
  ['farmers','farmer1',{firstName:'Alice',lastName:'Niyonzima',farmerCode:'FRM-0345',phone:'0790000001',district:'Gasabo'},'farmer'],
  ['harvest-forecasts','forecast10',{forecastDate:'2026-11-05',cropName:'Maize',expectedKg:1010,farmer:{id:'{{farmer5}}'},agronomist:{id:'{{agronomist3}}'}},'harvest forecast']]) {
  request(crud,`A2 ${route} get`,'GET',`${route}/{{${id}}}`);
  request(crud,`A2 ${route} update`,'PUT',`${route}/update/{{${id}}}`,200,body,label[0].toUpperCase()+label.slice(1)+' updated successfully');
  request(crud,`A2 ${route} missing update`,'PUT',`${route}/update/${missing}`,404,body,`The ${label} with that id does not exist`);
  request(crud,`A2 ${route} missing delete`,'DELETE',`${route}/delete/${missing}`,404,null,`The ${label} with that id does not exist`);
}
const collection = {info:{name:'Ikigega Crop Supply Forecasting System Midterm',schema:'https://schema.getpostman.com/json/collection/v2.1.0/collection.json',description:'Run on a fresh demo database. Historical seed dates require an application environment dated no later than 2026-10-20. Read README before running. Tests are provided, not claimed as executed.'},variable:[{key:'baseUrl',value:'http://localhost:8080'},{key:'missingId',value:'00000000-0000-0000-0000-000000000000'}],item:folders};
const disposable = folder('A2 - independent CRUD lifecycle');
for (const [route,body,label,predicate] of [
  ['crop-types',{name:'Disposable crop'},'Crop type',"x.name === 'Disposable crop'"],
  ['warehouses',{warehouseNumber:99,name:'Disposable warehouse'},'Warehouse','x.warehouseNumber === 99'],
  ['agronomists',{firstName:'Disposable',lastName:'Agronomist',phone:'0789999999',warehouse:null,cropTypes:[]},'Agronomist',"x.firstName === 'Disposable'"],
  ['farmers',{firstName:'Disposable',lastName:'Farmer',farmerCode:'FRM-DISPOSABLE',phone:'0799999999',district:'Gasabo'},'Farmer',"x.farmerCode === 'FRM-DISPOSABLE'"],
  ['harvest-forecasts',{forecastDate:'{{futureDate}}',cropName:'Disposable crop',expectedKg:99,farmer:null,agronomist:null},'Harvest forecast',"x.cropName === 'Disposable crop'"]
]) {
  request(disposable,`A2 ${route} save`,'POST',`${route}/save`,201,body,`${label} saved successfully`);
  capture(disposable,route,'disposableId',predicate);
  request(disposable,`A2 ${route} get`,'GET',`${route}/{{disposableId}}`);
  request(disposable,`A2 ${route} update`,'PUT',`${route}/update/{{disposableId}}`,200,body,`${label} updated successfully`);
  request(disposable,`A2 ${route} delete`,'DELETE',`${route}/delete/{{disposableId}}`,200,null,`${label} deleted successfully`);
  request(disposable,`A2 ${route} deleted get`,'GET',`${route}/{{disposableId}}`,404,null,`The ${label.toLowerCase()} with that id does not exist`);
}
fs.writeFileSync(path.join(__dirname,'..','ikigega-crop-supply-forecasting-system-midterm.postman_collection.json'), JSON.stringify(collection,null,2)+'\n');
console.log('Generated collection:', folders.reduce((n,f)=>n+f.item.length,0), 'requests');
