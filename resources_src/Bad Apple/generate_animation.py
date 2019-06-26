import os
import sys
import time
import numpy as np
import cv2
import gc
from PIL import Image
from matplotlib.image import imread
import matplotlib.pyplot as plt


cap = cv2.VideoCapture('./[HD] Touhou - Bad Apple!! [ＰＶ] (Shadow Art).webm')

black_delay = 1024
frameCount = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
frameWidth = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
frameHeight = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))

resolution = 512
frame_reduction = 3
print(frameCount)  # 6572
print(frameWidth)  # 960
print(frameHeight)  # 720
pad = int((960-720)/2)

num_batch = 1

for i in range(num_batch):
    fc = 0
    r = True

    buf = np.empty((512*int(frameCount/frame_reduction/num_batch+black_delay), 512),
                   np.dtype('uint8'))
    while (fc < frameCount/num_batch and r):
        r, frame = cap.read()
        if(fc % frame_reduction != 0):
            try:
                frame = frame[:, :, 0]
                frame = cv2.resize(frame, dsize=(
                    resolution, resolution), interpolation=cv2.INTER_NEAREST)
                # frame = np.pad(frame, ((pad, pad), (0, 0)), 'constant')
                count = int(fc/frame_reduction)
                buf[(black_delay+count)*resolution:(black_delay +
                    count+1)*resolution, :] = frame
                print(frame.shape)
            except:
                continue
        fc += 1
        print(fc)

    gc.collect()
    img = Image.fromarray(buf)
    img.save('./bad_apple.png')
    # plt.imsave('./bad_apple'+str(i)+'.png', buf)
