import os
from functools import cache
from unittest import result
from urllib import request
from urllib.request import Request

from fastapi import FastAPI, File, UploadFile, Form
from starlette.staticfiles import StaticFiles

from segmentation import get_yolov5, get_image_from_bytes
from starlette.responses import Response, HTMLResponse, FileResponse
import io
from PIL import Image
import json
from fastapi.middleware.cors import CORSMiddleware
import shutil

import aioredis
from fastapi import FastAPI, File, UploadFile, Form
from starlette.requests import Request
from starlette.responses import Response
from fastapi.templating import Jinja2Templates

model = get_yolov5()

app = FastAPI()

origins = [
    "http://localhost",
    "http://localhost:8000",
    "*"
]

templates = Jinja2Templates(directory="templates")

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/", response_class=HTMLResponse)
async def upload_image(request: Request):
    return templates.TemplateResponse("uploadview.html", {"request": request})


@app.post("/detect")
async def handle_form(request: Request, file: bytes = File(...)):
    input_image = get_image_from_bytes(file)
    results = model(input_image)
    detect_res = results.pandas().xyxy[0].to_json(orient="records")
    detect_res = json.loads(detect_res)
    number = str(len(detect_res))
    return {number}

    # input_image = get_image_from_bytes(file)
    # results = model(input_image)
    # detect_res = results.pandas().xyxy[0].to_json(orient="records")
    # detect_res = json.loads(detect_res)
    # number = str(len(detect_res))
    # results.render()
    # for img in results.imgs:
    #     bytes_io = io.BytesIO()
    #     img_base64 = Image.fromarray(img)
    #     img_base64.save(bytes_io, format="jpeg")
    # return templates.TemplateResponse("detect.html", context={"request": request}),


    # input_image = get_image_from_bytes(file)
    # results = model(input_image)
    # results.render()
    # for img in results.imgs:
    #     bytes_io = io.BytesIO()
    #     img_base64 = Image.fromarray(img)
    #     img_base64.save(bytes_io, format="jpeg")
    # return Response(content=bytes_io.getvalue(), media_type="image/jpeg")


@app.post("/img-to-json")
async def detect_people_return_json_result(file: bytes = File(...)):
    input_image = get_image_from_bytes(file)
    results = model(input_image)
    detect_res = results.pandas().xyxy[0].to_json(orient="records")
    detect_res = json.loads(detect_res)
    return {"result": detect_res}


@app.post("/img-to-img")
async def detect_people_return_base64_img(file: bytes = File(...)):
    input_image = get_image_from_bytes(file)
    results = model(input_image)
    results.render()
    for img in results.imgs:
        bytes_io = io.BytesIO()
        img_base64 = Image.fromarray(img)
        img_base64.save(bytes_io, format="jpeg")
    return Response(content=bytes_io.getvalue(), media_type="image/jpeg")


@app.post("/img-to-number")
async def detect_people_return_number(file: bytes = File(...)):
    input_image = get_image_from_bytes(file)
    results = model(input_image)
    detect_res = results.pandas().xyxy[0].to_json(orient="records")
    detect_res = json.loads(detect_res)
    number = str(len(detect_res))
    return {number}


@app.post("/file-to-number")
async def detect_people_return_number(file: UploadFile = File(...)):
    with open(file.filename, 'wb') as buffer:
        shutil.copyfileobj(file.file, buffer)
    results = model(file.filename)
    detect_res = results.pandas().xyxy[0].to_json(orient="records")
    detect_res = json.loads(detect_res)
    number = str(len(detect_res))
    os.remove(file.filename)
    return {number}
