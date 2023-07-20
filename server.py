import asyncio
import errno
import subprocess
import sys
import discord_webhook
import queue
import threading
import time

webhook = discord_webhook.Webhook("https://discord.com/api/webhooks/1131413404599455754/4o4kIdRTzONA2EtKamBz_3Nbpb8eodVy_KmWGAiUP4e3ThytupkFJmJ_oSinhDogXiFj")


async def listner(cmd):
    process = subprocess.Popen(
        cmd, 
        shell=True,
        stdout=subprocess.PIPE, 
        stderr=subprocess.STDOUT, 
        text=True
    )
    while process.poll() is None:
        try:
            await asyncio.sleep(0.8)
            # ノンブロッキングモードなので読める分だけ読む
            buf = process.stdout.read()
            if buf == None:
                continue
            try:
                buf = buf.decode()
            except Exception as e:
                print(str(e))
                pass
            print(buf)
            webhook.upload_to_discord(f"```json{buf}```")
        except IOError as e:
            # 読むべき内容がない場合はIOError(11,
            # "Resource temporarily unavailable")が
            # スローされるので待機
            if e.errno == errno.EAGAIN:
                await asyncio.sleep(1)
        except:
            exc_type, exc_obj, exc_tb = sys.exc_info()
            text = f"エラー:\ntype: {exc_type}\nobj: {exc_obj}\ntb : {exc_tb.tb_lineno}" #type: ignore
            print(text)



if __name__ == "__main__":
    command_to_execute = "java samepuzzle/Server" 
    
    asyncio.run(listner(command_to_execute))
    #t2 = threading.Thread(target=send_loop)
    #t2.start()