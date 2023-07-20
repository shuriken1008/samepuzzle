import requests
from requests_toolbelt.multipart import encoder
import datetime


class Webhook:
    webhook_url = ""

    def __init__(self, _url:str) -> None:
        self.webhook_url = _url

    def upload_to_discord(self, text, file_path=None, file_name=None):
        form = encoder.MultipartEncoder({
            "content": text,
            'file': (file_name, open(file_path, 'rb')) if not file_path == None else ""
            })
        r = requests.post(
            self.webhook_url,
            data=form,
            headers={"Content-Type": form.content_type}
        )
        #print(r.text)
        return r.text
