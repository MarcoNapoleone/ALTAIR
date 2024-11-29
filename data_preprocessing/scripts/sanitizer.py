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

def get_file_name(name):
    if '_' in name:
        name = name.split('_')[1]
    if 'arXiv' in name:
        name = name.replace('arXiv', '')

    return name

def sanitize_json(file):
    if len(file) == 0:
        raise ValueError('Skipping empty file')

    if 'global_footnotes' in file:
        raise Exception("File is not in the correct format.")

    new_file = {}
    for table_name, table_data in file.items():

        if not isinstance(table_data, dict):
           logger(f"Skipping non-table data for field [{table_name}].", log_type='warning')
           continue

        required_keys = {'caption', 'table', 'footnotes', 'references'}
        if not required_keys.issubset(table_data.keys()):
            logger(f"Skipping table with missing fields [{table_name}].", log_type='warning')
            continue

        table_data = {k: table_data.get(k, '' if k in ['caption', 'table'] else []) for k in required_keys}
        table_data['footnotes'] = [f for f in table_data['footnotes'] if isinstance(f, str)]
        table_data['references'] = [r for r in table_data['references'] if isinstance(r, str)]

        new_file[table_name] = {key: table_data[key] for key in required_keys}

    return new_file

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: python sanitizer.py <directory> <output_directory>")
        sys.exit(1)

    source_directory, output_directory = sys.argv[1], sys.argv[2]
    os.makedirs(output_directory, exist_ok=True)
    logfile = os.path.join(output_directory, 'log.txt')

    for filename in sorted(os.listdir(source_directory)):
        if filename.endswith('.json'):
            try:
                with open(os.path.join(source_directory, filename), 'r') as f:
                    data = json.load(f)

                filename = get_file_name(filename)
                data = sanitize_json(data)

                if len(data) == 0:
                    raise ValueError('Skipping file with no valid tables.')

                with open(os.path.join(output_directory, filename), 'w') as f:
                    json.dump(data, f, indent=4)
            except json.JSONDecodeError:
                logger(f"File {filename} is not in JSON format. Skipping...", log_type='error')
            except Exception as e:
                logger(f"Error: {e} in file {filename}.", log_type='error')

    logger("All files sanitized.")
    logger(f"Sanitized files saved in {output_directory}")
    total_files = len(os.listdir(source_directory))
    sanitized_files = len(os.listdir(output_directory))
    logger(f"Sanitized {sanitized_files} out of {total_files} files.")


# Usage: python sanitizer.py <directory> <output_directory>
