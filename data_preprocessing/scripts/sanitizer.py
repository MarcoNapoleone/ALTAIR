### parsing all the json in the directory and sanitizing the data
# rejecting the json files that are not json formatted
# the correct format is a dictionary with the following keys:
"""
{
    "wildcard_table_name": {
        "caption": "string",
        "table": "string",
        "footnotes": [
            "string",
            "string",
            "string"
        ],
        "references": [
            "string",
            "string",
            "string"
        ]
},
"""
# if some fields are missing, they should be filled with an empty string
# if the json is not in the correct format, the file should be rejected
# The script should output a new directory with the sanitized json files

import json
import os
import sys

from logger import logger

global logfile


def sanitize_json(file):
    # some files are not in the correct format, so we need to check if the file is in the correct format
    # one easy flag to check is if "global_footnotes" is in the file, if it is, then the file is not in the correct format
    if 'global_footnotes' in file:
        raise Exception("File is not in the correct format.")

    # iterate through the tables in the file
    for table_name, table_data in file.items():

        # Check if the table data is a dictionary
        if not isinstance(table_data, dict):
            logger(f"└──Skipping non-table data for table [{table_name}].", log_type='warning', file=logfile)
            continue

        # Ensure all required fields are present
        if 'caption' not in table_data:
            table_data['caption'] = ''
        if 'table' not in table_data:
            table_data['table'] = ''
        if 'footnotes' not in table_data:
            table_data['footnotes'] = []
        if 'references' not in table_data:
            table_data['references'] = []

        # Ensure fields have the correct data types
        if not isinstance(table_data['footnotes'], list):
            table_data['footnotes'] = []
        if not isinstance(table_data['references'], list):
            table_data['references'] = []
        if not isinstance(table_data['caption'], str):
            table_data['caption'] = ''
        if not isinstance(table_data['table'], str):
            table_data['table'] = ''

        # Filter out non-string items from lists
        table_data['footnotes'] = [f for f in table_data['footnotes'] if isinstance(f, str)]
        table_data['references'] = [r for r in table_data['references'] if isinstance(r, str)]

        # Update the file with sanitized data
        file[table_name] = table_data

    return file


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: python sanitizer.py <directory> <output_directory>")
        sys.exit(1)

    source_directory = sys.argv[1]
    output_directory = sys.argv[2]

    if not os.path.exists(output_directory):
        os.makedirs(output_directory)

    logfile = os.path.join(output_directory, 'log.txt')

    for filename in sorted(os.listdir(source_directory)):
        if filename.endswith('.json'):
            try:
                file_path = os.path.join(source_directory, filename)
                with open(file_path, 'r') as f:
                    data = json.load(f)

                sanitize_json(data)
                output_path = os.path.join(output_directory, filename)
                with open(output_path, 'w') as f:
                    json.dump(data, f, indent=4)
            except json.JSONDecodeError:
                logger(f"File {filename} is not in JSON format. Skipping...", log_type='error', file=logfile)
                continue
            except Exception as e:
                logger(f"Error: {e} in file {filename}.", log_type='error', file=logfile)
                continue

    logger("All files sanitized.", file=logfile)

    # Log the output directory
    logger(f"Sanitized files saved in {output_directory}", file=logfile)

    # calculate the number of files sanitized vs the number of files in the directory
    total_files = len(os.listdir(source_directory))
    sanitized_files = len(os.listdir(output_directory))
    logger(f"Sanitized {sanitized_files} out of {total_files} files.", file=logfile)

# Usage: python sanitizer.py <directory> <output_directory>
