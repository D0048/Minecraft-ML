import os
import sys
import time
import numpy as np
import cv2
from matplotlib.image import imread
import matplotlib.pyplot as plt


original = imread('./ml_scalar.png')
print(original.shape)
# White stride at [:,255,:]
resolution = 32
width_step = (1/resolution)
print(original[:, 255, :])

for i in range(resolution):
    original[:, 255, :] -= width_step
    plt.imsave('./ml_scalar_value_'+str(i)+'.png', original)
