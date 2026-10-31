const fs = require('node:fs');
const vm = require('node:vm');
const assert = require('node:assert/strict');
const path = require('node:path');
const collection = JSON.parse(fs.readFileSync(path.join(__dirname,'..','ikigega-crop-supply-forecasting-system-midterm.postman_collection.json')));
const vars = new Map(collection.variable.map(x=>[x.key,x.value]));
vars.set('baseUrl', process.env.IKIGEGA_TEST_URL || 'http://localhost:18080');
const shift = process.argv.includes('--shift-dates');
let count = 0;
function expect(actual, negated = false) {
  const chain = new Proxy({}, { get(_, prop) {
    if (prop === 'not') return expect(actual, !negated);
    const check = (ok, msg) => assert.ok(negated ? !ok : ok, msg);
    if (prop === 'eql') return expected => { if (negated) assert.notDeepEqual(actual,expected); else assert.deepEqual(actual,expected); };
    if (prop === 'include') return expected => check(actual.includes(expected),`Expected ${JSON.stringify(actual)} to include ${expected}`);
    if (prop === 'least') return expected => check(actual >= expected,`Expected ${actual} >= ${expected}`);
    if (prop === 'property') return key => check(Object.hasOwn(actual,key),`Property ${key}`);
    if (prop === 'exist') { check(actual != null,'Expected record to exist'); return chain; }
    return chain;
  }});
  return chain;
}
function shifted(text) {
  if (!shift) return text;
  const base = Date.UTC(2026,9,20);
  const now = new Date();
  const tomorrow = Date.UTC(now.getFullYear(),now.getMonth(),now.getDate()+1);
  return text.replace(/2026-(?:10|11)-\d{2}/g, date => new Date(Date.parse(date+'T00:00:00Z')-base+tomorrow).toISOString().slice(0,10));
}
function interpolate(text) { return shifted(text.replace(/\{\{([^}]+)\}\}/g, (_,key) => { assert.ok(vars.has(key),`Missing collection variable ${key}`); return vars.get(key); })); }
async function main() {
  for (const folder of collection.item) {
    console.log(folder.name);
    for (const item of folder.item) {
      const pm = { collectionVariables:{get:k=>vars.get(k),set:(k,v)=>vars.set(k,v)},expect,test:(name,fn)=>{fn();count++;} };
      const run = event => vm.runInNewContext(event.script.exec.join('\n'),{pm,Date,Set,console}, {timeout:2000});
      for (const event of item.event || []) if (event.listen === 'prerequest') run(event);
      const response = await fetch(interpolate(item.request.url),{method:item.request.method,headers:Object.fromEntries(item.request.header.map(x=>[x.key,x.value])),body:item.request.body ? interpolate(item.request.body.raw) : undefined,signal:AbortSignal.timeout(15000)});
      const text = await response.text();
      pm.response = {text:()=>text,json:()=>JSON.parse(text),to:{have:{status:code=>assert.equal(response.status,code,`${item.name}: ${text}`)}}};
      try { for (const event of item.event || []) if (event.listen === 'test') run(event); }
      catch (err) { throw new Error(`${item.name}: ${err.message}`,{cause:err}); }
    }
  }
  console.log(`PASS: ${count} assertions; historical dates ${shift ? 'shifted to equivalent future dates' : 'unchanged'}.`);
}
main().catch(err=>{console.error(err);process.exitCode=1;});
