package it.uniroma3.idd.HW_2.controller;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/document/{id}")
    public Document getDocument(@PathVariable Long id) {
        return documentService.getDocument(id);
    }

    @GetMapping("/document/query")
    public Document getDocumentQuery(@RequestParam String query) {
        return documentService.getDocumentQuery(query);
    }

    @GetMapping("/document")
    public Collection<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @GetMapping("/document/location")
    public Collection<Document> getAllDocumentsByLocation(@RequestParam String location) {
        return documentService.getAllDocumentsByLocation(location);
    }
}