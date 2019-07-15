import os
import sys
import time
import numpy as np
import cv2
from matplotlib.image import imread
import matplotlib.pyplot as plt


original = np.ones([1,1,3])
# White stride at [:,255-10,:]
resolution = 16
width_step = (1/resolution)

for i in range(resolution):
    original[:] -= width_step
    plt.imsave('./ml_scalar_value_'+str(i)+'.png', original)
