import datetime
import os
import sys
import requests
from lxml import etree
from logger import logger

global logfile


def download_articles(query, k):
    batch_size = 25  # The maximum number of articles returned per query

    if k <= batch_size:
        fetch_articles(query, 0, k, batch_size)
    else:
        # Download articles in batches of size `batch_size`
        for i in range(0, k, batch_size):
            successful_downloads = fetch_articles(query, i, min(batch_size, k - i), batch_size)
            if successful_downloads == 0:
                logger(f"No articles found in batch {i + 1}-{i + min(batch_size, k - i)}", log_type='warning',
                       file=logfile)


def fetch_articles(query, start, k, batch_size=25):
    """Fetches and downloads articles from arXiv, returns the number of successful downloads."""
    try:
        # Encode the query for use in the URL
        query_encoded = '+'.join(query.split())
        url = f"https://arxiv.org/search/?query={query_encoded}&searchtype=all&source=header&size={batch_size}&order=-announced_date_first&start={start}"
        logger(f"Fetching search results for '{query}'", file=logfile)

        # Send a GET request to the arXiv search page
        response = requests.get(url)
        response.raise_for_status()  # Check for request errors
        logger(f"Successfully fetched search results for '{query}'", file=logfile)

        # Parse the HTML content
        root = etree.HTML(response.content)

        with open('./logs/read.me', 'a') as file_logs:
            file_logs.write(query + '\n')

        # Initialize a counter for successful downloads
        successful_downloads = 0

        articles = root.xpath("//p[@class='list-title is-inline-block']/a/@href")

        # Iterate over article URLs in the search results
        for idx, article_url in enumerate(articles):
            if successful_downloads >= k:
                logger("Batch limit reached. Stopping further downloads.", file=logfile)
                break

            logger(f"Processing article {idx + 1 + start}: {article_url}", file=logfile)

            try:
                # Fetch the article page
                article_response = requests.get(article_url)
                article_response.raise_for_status()

                # Parse the article page
                article_root = etree.HTML(article_response.content)

                # Check for the LateXML download link
                if article_root.xpath("//*[@id='latexml-download-link']"):
                    # Get the URL for the downloadable HTML
                    href = article_root.xpath("//*[@id='latexml-download-link']/@href")[0]
                    html_response = requests.get(href)
                    html_response.raise_for_status()

                    with open('./logs/read.me', 'a') as file_logs:
                        file_logs.write(href + '\n')

                    # Save the HTML content to a file
                    file_name = f"./sources/{os.path.basename(href)}.html"
                    with open(file_name, 'wb') as f:
                        f.write(html_response.content)
                    logger(f"└── Downloaded and saved HTML file: {file_name}", file=logfile)

                    successful_downloads += 1  # Increment counter for successful downloads
                else:
                    logger(f"└── No downloadable HTML found for article: {article_url}", log_type='warning',
                           file=logfile)
            except requests.RequestException as e:
                logger(f"Error fetching article {article_url}: {e}", log_type='error', file=logfile)
            except Exception as e:
                logger(f"Error processing article {article_url}: {e}", log_type='error', file=logfile)

        logger(f"Batch completed. Total successful downloads in this batch: {successful_downloads}", file=logfile)
        return successful_downloads
    except requests.RequestException as e:
        logger(f"Error fetching search results: {e}", log_type='error', file=logfile)
        return 0
    except Exception as e:
        logger(f"An unexpected error occurred: {e}", log_type='error', file=logfile)
        return 0


if __name__ == '__main__':
    # Check if the correct number of arguments is provided
    if len(sys.argv) != 3:
        print('Usage: python scraper.py <query> <k>')
        sys.exit(1)

    query = sys.argv[1]

    try:
        k = int(sys.argv[2])  # Ensure k is an integer
        if k <= 0:
            raise ValueError
    except ValueError:
        print("Error: 'k' must be a positive integer.")
        sys.exit(1)

    # Ensure the sources directory exists
    if not os.path.exists('./sources'):
        os.makedirs('./sources')

    # Start downloading articles
    # The logfile is set to the query string with spaces replaced by underscores and datatime appended
    logfile = query.replace(' ', '_').lower() + datetime.datetime.now().strftime("_%Y-%m-%d_%H-%M") + '.log'
    download_articles(query, k)