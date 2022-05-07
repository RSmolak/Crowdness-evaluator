import json

import PIL
from PIL import Image
import numpy as np
import torch


with open('C:\\Studia\\semestr_6\\PZ\\annotation_train.odgt', 'r+') as f:
    datalist = f.readlines()


# Model
#model = torch.hub.load('ultralytics/yolov5', 'yolov5s')  # or yolov5n - yolov5x6, custom

# Images
#img = 'https://ultralytics.com/images/zidane.jpg'  # or file, Path, PIL, OpenCV, numpy, list

# Inference
#results = model(img)

# Results
#results.print()  # or .show(), .save(), .crop(), .pandas(), etc.

inputfile = []
inner = {}
j=0
for i in np.arange(len(datalist)):
    adata = json.loads(datalist[i])
    gtboxes = adata['gtboxes']
    for gtbox in gtboxes:
        if gtbox['tag'] == 'person':
            inner = {
                'filename': adata['ID'],
                'name': 'person',
                'bndbox': gtbox['vbox']
            }
            file1 = open('C:\\Studia\\semestr_6\\PZ\\yolov5-master\\data\\imagesAndLabels\\labels\\' + adata['ID']+'.txt', "a+")
            image = PIL.Image.open('C:\\Studia\\semestr_6\\PZ\\yolov5-master\\data\\imagesAndLabels\\images\\' + adata['ID']+'.jpg')
            width, height = image.size
            val = [gtbox['vbox'][0]/width, gtbox['vbox'][1]/height,
                   gtbox['vbox'][2]/width, gtbox['vbox'][3]/height]
            for k in range(4):
                if val[k] <= 0:
                    val[k] = 0.000001
                elif val[k] >= 1:
                    val[k] = 0.999999

            str1 = str(0) + ' ' + str("{:.6f}".format(val[0])) + ' ' + str("{:.6f}".format(val[1])) + ' ' + str("{:.6f}".format(val[2])) + ' ' + str("{:.6f}".format(val[3])) + '\n'

            file1.write(str1)
            file1.close()
            inputfile.append(inner)

inputfile = json.dumps(inputfile)

with open('train.txt', 'a+') as f:
    f.write(str(inputfile))