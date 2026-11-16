"""Insert the 31 figures into the Word-valid test_noimg.docx using python-docx,
which embeds images with correct, unique drawing IDs that Word accepts.
docx-js corrupts multi-image documents; this sidesteps it entirely while reusing
all the text/tables/code/TOC that already open cleanly."""
import os, re
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH

ROOT = os.path.join(os.path.dirname(__file__), '..')
def ss(f):  return os.path.join(ROOT, 'SS', f)
def dia(f): return os.path.join(ROOT, 'diagrams', f)

# (path, max display width px) in the SAME order as build.js calls figure()
FIGS = [
    (ss('Keruyun.png'),520),(ss('Toast_POS.png'),520),(ss('HPP_calc.png'),340),
    (dia('usecase.png'),580),(dia('architecture.png'),300),(dia('erd_user.png'),620),
    (ss('login.png'),340),(ss('dashboard-admin.png'),580),(ss('dashboard_worker.png'),580),(ss('users.png'),580),
    (ss('materials.png'),580),(ss('materials_form.png'),340),(ss('inventory.png'),580),
    (ss('inventory_history.png'),580),(ss('low_stock.png'),580),(ss('shopping_list.png'),580),
    (ss('products.png'),580),(ss('BOM.png'),580),(ss('Unit_Cost.png'),580),(ss('overhead.png'),580),(ss('price_sensitivity.png'),580),
    (ss('batch_form.png'),580),(ss('batch_history.png'),580),(ss('stock_pre-flight.png'),360),
    (ss('sales_list.png'),580),(ss('sales_new.png'),520),(ss('Order_detail.png'),520),
    (ss('Pref.png'),580),(ss('indonesian.png'),580),(ss('mobile.png'),320),
]

doc = Document(os.path.join(ROOT, 'test_noimg.docx'))

idx = 0
for para in list(doc.paragraphs):
    txt = para.text.strip()
    m = re.match(r'^\[Figure (\d+)\.\s*(.*)\]$', txt)
    if not m:
        continue
    caption = f"Figure {m.group(1)}. {m.group(2)}"
    path, maxw = FIGS[idx]
    idx += 1
    width_in = min(maxw / 96.0, 6.0)

    # 1. image paragraph inserted BEFORE the placeholder
    img_p = para.insert_paragraph_before()
    img_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    img_p.add_run().add_picture(path, width=Inches(width_in))

    # 2. turn the placeholder into the caption (centered italic grey, no brackets)
    for r in list(para.runs):
        r._element.getparent().remove(r._element)
    para.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = para.add_run(caption)
    run.italic = True
    run.font.size = Pt(9)
    run.font.color.rgb = RGBColor(0x6B, 0x5D, 0x52)

out = os.path.join(ROOT, os.environ.get('OUT', 'CostCraft_Thesis_FINAL.docx'))
doc.save(out)
print(f"inserted {idx} figures -> {out}")
