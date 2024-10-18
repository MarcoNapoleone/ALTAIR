
source ./venv/bin/activate
pip install -r requirements.txt

python ./scripts/scraper.py "Recommender systems" 50
python ./scripts/parser.py ./sources/ ./extraction/
python ./scripts/json2csv.py ./extraction/ ./dataset/tables.csv

