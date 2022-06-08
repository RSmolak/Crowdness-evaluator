import requests
import os
from os.path import dirname, join




def main(filePath):

    #os.environ[filePath] = ":".join([p for p in os.environ[filePath].split(":")
    #                           if os.access(p, os.R_OK | os.X_OK)])


    #fileName = join(dirname(__file__),filePath)

    file = open(filePath, 'rb')
    img = requests.post("https://crowdeval-api-2iagetvpbq-uc.a.run.app/file-to-number",
                    files={"file": file})
    return img.text
