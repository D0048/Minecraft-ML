import os
import sys
import time
import numpy as np
import cv2
import json
from matplotlib.image import imread
import matplotlib.pyplot as plt


resolution = 32
base_json = {'value_xx': {'parent': 'block/cube',
                         'textures': {'all': 'minecraft_ml:ml_scalar_values_minimum/ml_scalar_value_xx.obj'}}}
# Should be ml_scalar_value_x
# print(json.dumps(base_json, indent=4))

for i in range(resolution):
    print(json.dumps(base_json).replace('xx', str(i))[1:-1]+',')
    pass
