import torch
from matplotlib import pyplot as plt
import numpy as np
import cv2

model = torch.hub.load('ultralytics/yolov5', 'yolov5s')
model

img = 'https://www.mylivestreams.com/images/camera/large_11154.jpg'
results = model(img)
results.print()

plt.imshow(np.squeeze(results.render()))
plt.savefig("obraz.png")
