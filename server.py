import subprocess

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
            print(output.strip())
    return process.poll()

if __name__ == "__main__":
    command_to_execute = "java samepuzzle/Server"  # 実行したいbashコマンドをここに入力してください
    execute_bash_command(command_to_execute)
