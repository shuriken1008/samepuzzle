import subprocess
import discord_webhook

webhook = discord_webhook.Webhook("https://discord.com/api/webhooks/1131413404599455754/4o4kIdRTzONA2EtKamBz_3Nbpb8eodVy_KmWGAiUP4e3ThytupkFJmJ_oSinhDogXiFj")

def execute_bash_command(command):
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
            webhook.upload_to_discord(f"```json\n{out}```")
    return process.poll()

if __name__ == "__main__":
    command_to_execute = "java samepuzzle/Server"  # 実行したいbashコマンドをここに入力してください
    execute_bash_command(command_to_execute)
