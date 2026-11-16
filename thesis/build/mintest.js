const fs=require('fs');const path=require('path');
const {Document,Packer,Paragraph,TextRun,ImageRun,HeadingLevel,TableOfContents,AlignmentType}=require('docx');
const SS=path.join(__dirname,'..','SS');
function build(name, kids){
  const doc=new Document({sections:[{children:kids}]});
  return Packer.toBuffer(doc).then(b=>fs.writeFileSync(path.join(__dirname,'..',name),b));
}
(async()=>{
  // A: text only
  await build('test_A_text.docx',[new Paragraph({children:[new TextRun('Hello world. Text only test.')]})]);
  // B: text + one image
  await build('test_B_image.docx',[
    new Paragraph({children:[new TextRun('Image test below:')]}),
    new Paragraph({children:[new ImageRun({type:'png',data:fs.readFileSync(path.join(SS,'login.png')),transformation:{width:300,height:155},altText:{title:'t',description:'d',name:'n'}})]})
  ]);
  // C: text + TOC + heading
  await build('test_C_toc.docx',[
    new Paragraph({heading:HeadingLevel.HEADING_1,children:[new TextRun('Contents')]}),
    new TableOfContents('TOC',{hyperlink:true,headingStyleRange:'1-3'}),
    new Paragraph({heading:HeadingLevel.HEADING_1,children:[new TextRun('Chapter 1')]}),
    new Paragraph({children:[new TextRun('Body text.')]})
  ]);
  console.log('built A(text), B(image), C(toc)');
})();
