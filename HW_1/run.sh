
source ./venv/bin/activate
pip install requirements.txt -r

python ./scripts/scraper.py "Recommender systems" 500
python ./scripts/parser.py ./sources/ ./extraction/
python ./scripts/json2csv.py ./extraction/ ./dataset/tables.csv

