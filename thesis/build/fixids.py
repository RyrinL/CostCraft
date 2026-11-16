"""Renumber BOTH drawing id attributes (wp:docPr and pic:cNvPr) to unique,
non-zero values so Microsoft Word accepts the file. docx-js leaves them all
the same (docPr id=1, cNvPr id=0), which Word treats as corruption.
Surgically rewrites only word/document.xml; every other part is copied verbatim
(no pack.py, no condensing)."""
import zipfile, re, sys, shutil, os

src, dst = sys.argv[1], sys.argv[2]
zin = zipfile.ZipFile(src)
doc = zin.read('word/document.xml').decode('utf-8')

c1 = [0]
def r1(m):
    c1[0] += 1
    return f'<wp:docPr id="{c1[0]}" name="Picture {c1[0]}"'
doc = re.sub(r'<wp:docPr id="\d+" name="[^"]*"', r1, doc)

c2 = [1000]
def r2(m):
    c2[0] += 1
    return f'<pic:cNvPr id="{c2[0]}" name="Picture {c2[0]}"'
doc = re.sub(r'<pic:cNvPr id="\d+" name="[^"]*"', r2, doc)

tmp = dst + '.tmp'
with zipfile.ZipFile(tmp, 'w', zipfile.ZIP_DEFLATED) as zout:
    for item in zin.infolist():
        data = doc.encode('utf-8') if item.filename == 'word/document.xml' else zin.read(item.filename)
        zout.writestr(item, data)
zin.close()
if os.path.exists(dst):
    os.remove(dst)
shutil.move(tmp, dst)

# verify
z = zipfile.ZipFile(dst)
d = z.read('word/document.xml').decode('utf-8')
a = re.findall(r'<wp:docPr id="(\d+)"', d)
b = re.findall(r'<pic:cNvPr id="(\d+)"', d)
print(f"docPr: {len(a)} ids, {len(set(a))} unique | cNvPr: {len(b)} ids, {len(set(b))} unique")
print("OK" if len(a) == len(set(a)) and len(b) == len(set(b)) and '0' not in b else "STILL BAD")
