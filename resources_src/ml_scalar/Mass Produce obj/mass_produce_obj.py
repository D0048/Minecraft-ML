import os
import sys
import time
import numpy as np
import cv2
from matplotlib.image import imread
import matplotlib.pyplot as plt


objfile = open('./ml_scalar.obj', 'r')
mtlfile = open('./ml_scalar.mtl', 'r')
objfile = objfile.read()
mtlfile = mtlfile.read()

resolution = 32

for i in range(resolution):
    new_name = 'ml_scalar_value_'+str(i)
    obj = objfile.replace('ml_scalar', new_name)
    mtl = mtlfile.replace('ml_scalar', new_name)
    open(new_name+'.obj', "w").write(obj)
    open(new_name+'.mtl', "w").write(mtl)
    pass
