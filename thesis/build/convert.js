// Render the 4 SVG diagrams to high-res PNG for Word compatibility.
const sharp = require('sharp');
const fs = require('fs');
const path = require('path');

const dir = path.join(__dirname, '..', 'diagrams');
const files = ['architecture', 'usecase', 'sequence', 'er'];

(async () => {
  for (const f of files) {
    const svg = path.join(dir, f + '.svg');
    const png = path.join(dir, f + '.png');
    await sharp(svg, { density: 200 }).png().toFile(png);
    const meta = await sharp(png).metadata();
    console.log(`${f}.png  ${meta.width}x${meta.height}`);
  }
})();
