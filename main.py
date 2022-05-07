import json
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
            file1 = open('C:\\Studia\\semestr_6\\PZ\\projekt\\zdjecia_txt\\' + adata['ID']+'.txt', "a+")
            str1 = str(0) + ' '  + str(gtbox['vbox'][0]) + ' '  + str(gtbox['vbox'][1]) + ' '  + \
                   str(gtbox['vbox'][2]) + ' '  + str(gtbox['vbox'][3]) + '\n'

            file1.write(str1)
            file1.close()
            inputfile.append(inner)

inputfile = json.dumps(inputfile)

with open('train.txt', 'a+') as f:
    f.write(str(inputfile))