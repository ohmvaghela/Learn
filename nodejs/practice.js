
const tick = Date.now();
const log = (v) => {
  console.log(`${v} \nElapsed: ${Date.now() - tick} ms`);
};

const url = `https://api.github.com/users/hadley/orgs`

const delay = ms => new Promise(resolve => setTimeout(resolve, ms));


const block = async () =>{
  // let i = 0;
  // while(i<1e9){i++}
  // log(`while done`)
  await delay(1000);

  return "done"
}


const main = async () =>{
  // code 1
  const result1 = await block();
  const result2 = await block();
  return [result1,result2];
  // // code 2
  // const result1 = await block();
  // const result2 = await block();
  // return Promise.all([result1,result2]);
}
log('Step 1 complete');

main().then(result=>{
  log(result);
}); 
log('Step 2 complete');
