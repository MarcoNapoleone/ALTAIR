import json
import sys

import pandas as pd
import os


def extract_data_from_json(file_path):
    with open(file_path, 'r') as file:
        data = json.load(file)

    rows = []

    for table_id, table_data in data.items():
        caption = table_data.get('caption', '')
        table = "; ".join(table_data.get('table', []))
        footnotes = "; ".join(table_data.get('footnotes', []))
        references = "; ".join(table_data.get('references', []))

        rows.append({
            'id': table_id,
            'caption': caption,
            'table': table,
            'footnotes': footnotes,
            'references': references,
            'id_file': file_path
        })

    return rows


def create_csv_from_folder(json_folder, output_csv):
    all_rows = []

    for json_file in os.listdir(json_folder):
        if json_file.endswith('.json'):
            file_path = os.path.join(json_folder, json_file)
            all_rows.extend(extract_data_from_json(file_path))

    # Create DataFrame and save to CSV
    df = pd.DataFrame(all_rows)
    df.to_csv(output_csv, index=False)
    print(f"CSV saved to {output_csv}")


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print('Usage: python json2csv.py <json_folder> <output_csv_file>')
        sys.exit(1)

    json_folder = sys.argv[1]
    output_csv = sys.argv[2]
    create_csv_from_folder(json_folder, output_csv)
