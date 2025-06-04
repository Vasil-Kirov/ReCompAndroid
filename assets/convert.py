from PIL import Image
import sys

if len(sys.argv) < 2:
    print("No file given as argument")
    exit()


img = Image.open(sys.argv[1]).convert('RGBA')
img.save(f'{sys.argv[1].split('.')[0]}.bmp', 'BMP')

