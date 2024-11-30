import torch
from transformers import BertModel, AutoTokenizer

# load model and tokenizer
model_id = "bert-base-uncased"
model = BertModel.from_pretrained(model_id)
tokenizer = AutoTokenizer.from_pretrained(model_id)

# Dummy input for exporting
dummy_model_input = tokenizer("This is a sample", return_tensors="pt")

# Export to ONNX format
torch.onnx.export(
    model,
    tuple(dummy_model_input.values()),
    f="bertEmbedding2.onnx",
    input_names=['input_ids', 'attention_mask'],
    output_names=['last_hidden_state'],  # Cambiato in last_hidden_state
    dynamic_axes={'input_ids': {0: 'batch_size', 1: 'sequence'},
                  'attention_mask': {0: 'batch_size', 1: 'sequence'},
                  'last_hidden_state': {0: 'batch_size', 1: 'sequence'}},
    do_constant_folding=True,
    opset_version=16,  # Versione ONNX aggiornata (se supportata)
)
