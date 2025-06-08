
def hex_to_rgb(hex):
    h = str(hex).lstrip('#')
    return list(int(h[i:i+2], 16) for i in (0, 2, 4))

"""
Background
Primary
Secondary
Accent
Text
"""

hexs = """
#F5F7FA
#3A86FF
#8338EC
#FF006E
#222222
"""

hex_array = hexs.split()

for item in hex_array:
    if len(item) == 0:
        continue
    rgb = hex_to_rgb(item)
    rgb[0] = rgb[0] / 255
    rgb[1] = rgb[1] / 255
    rgb[2] = rgb[2] / 255
    print('v4 {', rgb[0], ',', rgb[1], ',', rgb[2], '},')
