import subprocess
import discord_webhook
import queue
import asyncio

webhook = discord_webhook.Webhook("https://discord.com/api/webhooks/1131413404599455754/4o4kIdRTzONA2EtKamBz_3Nbpb8eodVy_KmWGAiUP4e3ThytupkFJmJ_oSinhDogXiFj")

q = queue.Queue()

async def asyncio_main(cmd):
    asyncio.ensure_future(execute_bash_command(cmd))
    asyncio.ensure_future(send_loop())


async def execute_bash_command(command):
    process = subprocess.Popen(
        command, 
        shell=True,
        stdout=subprocess.PIPE, 
        stderr=subprocess.STDOUT, 
        text=True
    )
    while True:
        output = process.stdout.readline()
        if output == '' and process.poll() is not None:
            break
        if output:
            out = str(output.strip())
            print(out)
            q.put(f"```json\n{out}```")
            
    return process.poll()




async def send_loop():
    while True:
        try:
            if q.empty:
                continue
            webhook.upload_to_discord(q.get())
            await asyncio.sleep(0.8)
            pass
        except Exception as e:
            print(e)


if __name__ == "__main__":
    command_to_execute = "java samepuzzle/Server" 
    asyncio.run(asyncio_main(command_to_execute))
