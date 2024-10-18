import json
import os
import re
import sys
import datetime
from lxml import etree
from logger import logger

global logfile


def get_ref_dict(root_html):
    # Extract references
    references_dict = {}
    all_references = root_html.xpath("//*[contains(@class, 'ltx_ref')]")

    for ref in all_references:
        ref_href = ref.get('href')

        # Check if ref_href is not None
        if ref_href and '#' in ref_href:
            ref_href = ref_href.split('#')[1]

        if ref_href:  # Ensure ref_href is valid before proceeding
            paragraph = ref.xpath("./ancestor::p[@class='ltx_p'][1]")

            if len(paragraph) > 0:
                if ref_href in references_dict:
                    references_dict[ref_href].append(paragraph[0])
                else:
                    # if the reference is not in the dictionary, add it
                    references_dict[ref_href] = [paragraph[0]]

    return references_dict


def parser(html, filename):
    # Parse the HTML content
    root = etree.HTML(html)

    # Initialize the dictionary to store the extracted data
    data = {}

    # Extract references
    references_dict = get_ref_dict(root)

    tables = root.xpath("//*[contains(@class, 'ltx_table')][.//*[contains(@class, 'ltx_tabular')]]")

    for t in tables:

        table_data = {}
        table_id = t.get('id')

        # initialize the table_data json
        table_data["caption"] = ''
        table_data["table"] = ''
        table_data["footnotes"] = []
        table_data["references"] = []

        # Extract the table caption
        caption_nodes = t.xpath(".//figcaption//node()")

        for node in caption_nodes:
            if isinstance(node, etree._ElementUnicodeResult):
                table_data["caption"] += node
            elif node.tag == 'span':
                table_data["caption"] += ''
            else:
                table_data["caption"] += etree.tostring(node, pretty_print=True).decode()

        # Extract the table content
        tables_html = t.xpath(".//*[contains(@class, 'ltx_tabular')]")  # Xpath to extract table
        for t_html in tables_html:
            table_data["table"] += etree.tostring(t_html, pretty_print=True).decode()

        # Extract footnotes
        footnotes = t.xpath(".//p[not(ancestor::*[contains(@class, 'ltx_tabular')])]")
        for f in footnotes:
            table_data["footnotes"].append(etree.tostring(f, pretty_print=True).decode())

        # Extract references
        references = references_dict.get(table_id, [])
        for r in references:
            #if r is a string, add it to the references
            if isinstance(r, str):
                table_data["references"].append(r)
            else:
                ref_elements = r.xpath(".//node()")
                par = ''
                for e in ref_elements:
                    if isinstance(e, etree._ElementUnicodeResult):
                        par += e
                    elif e.tag == 'span' or e.tag == 'em' or e.tag == 'a':
                        par += ''
                    else: par += etree.tostring(e, pretty_print=True).decode()

                table_data["references"].append(par)

        # Add the extracted data to the json
        data[table_id] = table_data

    if data == {}:
        logger(f'No tables found in the HTML file {filename}', log_type='warning', file=logfile)

    return data


def save_json(data, filename, path='output'):
    # Save the extracted data to a JSON file

    if not os.path.exists(path):
        os.makedirs(path)
    with open(os.path.join(path, filename), 'w') as f:
        json.dump(data, f, indent=4)


if __name__ == '__main__':
    # Check if the correct number of arguments is provided
    if len(sys.argv) != 3:
        print('Usage: python parser.py <source_directory> <output_directory>')
        sys.exit(1)

    source_path = sys.argv[1]
    output_path = sys.argv[2]

    logfile = f"""parser{source_path.split('/')[-1]}{datetime.datetime.now().strftime("_%Y-%m-%d_%H-%M")}.log"""

    # Ordina i file alfabeticamente
    for filename in sorted(os.listdir(source_path)):
        if filename.endswith(".html"):

            try:
                file_path = os.path.join(source_path, filename)
                with open(file_path, 'r') as f:
                    html = f.read()

                # Parse the HTML content
                data = parser(html, filename)
                logger(f"Extracted data from HTML file: {file_path}", file=logfile)

                # Save the extracted data to a JSON file
                json_filename = f"{filename.replace('.html', '.json')}"
                save_json(data, json_filename, path=output_path)
                logger(f"└── Saved extracted data to JSON file: {json_filename}", file=logfile)
            except Exception as e:
                logger(f"Error: {e}", log_type='error', file=logfile)

    logger("All HTML files processed.", file=logfile)
