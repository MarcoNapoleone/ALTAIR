import datetime
import os
from rich.console import Console

console = Console()

def logger(message, log_type='info', file=None):
    # Get the current date and time
    now = datetime.datetime.now()
    timestamp = now.strftime('%Y-%m-%d %H:%M:%S')

    # Define the log message format
    log_message = f"[{timestamp}] [{log_type.upper()}] {message}"

    # Print the log message to the console
    color = 'green' if log_type == 'info' else 'yellow' if log_type == 'warning' else 'red'
    console.print(log_message, style=f'bold {color}')

    # Write the log message to a file if a filename is provided
    file = f'logs/{file}' if file else None

    if file and not os.path.exists(os.path.dirname(file)):
        os.makedirs(os.path.dirname(file))

    if file:
        with open(file, 'a') as f:
            f.write(log_message + '\n')
    else:
        # If no filename is provided, create a new log file
        with open(f'logs/log_{now.strftime("%Y%m%d")}.txt', 'a') as f:
            f.write(log_message + '\n')

    return log_message