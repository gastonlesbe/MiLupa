# Builds banner_es_milupa_1024x500.png: the current Spanish feature graphic with
# the name "MyLens" replaced by "MiLupa" (the Spanish store name).
from PIL import Image, ImageDraw, ImageFont

DIR = "/home/gaston/StudioProjects/MiLupa/imagenes/"
im = Image.open(DIR + "banner_es_1024x500.png").convert("RGB")
d = ImageDraw.Draw(im)

# The background is a pure vertical gradient: repaint each row of the old name
# with the colour from the row's left edge.
x0, x1, y0, y1 = 150, 470, 300, 388
for y in range(y0, y1):
    d.line([(x0, y), (x1, y)], fill=im.getpixel((20, y)))

font = ImageFont.truetype("/usr/share/fonts/truetype/ubuntu/Ubuntu[wdth,wght].ttf", 80)
font.set_variation_by_name("Light")
text = "MiLupa"
w = d.textlength(text, font=font)
cx = 305  # horizontal centre of the icon and subtitle
d.text((cx - w / 2, 296), text, font=font, fill=(255, 255, 255))
im.save(DIR + "banner_es_milupa_1024x500.png", optimize=True)
print("ok", w)
