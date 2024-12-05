# ALTAIR

## Description
Advanced Literature's Table Access Information Retrieval engine
This project implements a scientific paper search engine. 
The search engine is built using Apache Lucene, a high-performance, full-featured text search engine library.
The REST backend is built using Spring Boot. The Python scripts are used for data processing tasks.

## Usage
- **Java Application**: The main application logic is implemented in Java using Spring Boot. It can be accessed via the configured endpoints.
- **Python Scripts**: The Python scripts can be used for various data processing tasks.

To test the search engine, you can use the following URL:
[altair-search.web.app](http://altair-search.web.app)

![alt text](./images/example_usage.png)

## Setup Instructions

### Java and Spring Boot
1. **Install Maven**: Make sure Maven is installed on your system.
2. **Build the Project**: Navigate to the project directory and run:
    ```sh
   cd search_engine
    mvn clean install
    ```
3. **Run the Application**: Use the following command to run the Spring Boot application:
    ```sh
    mvn spring-boot:run
    ```

### Python
1. **Install pip**: Ensure pip is installed on your system.
2. **Run Python Scripts**: Execute the desired Python scripts using:
    ```sh
    .data_preprocessing/run.sh
    ```

## Technologies Used
- **Python**: Used for scripting and data processing.
- **pip**: Python package installer used to manage dependencies.
- **Java**: Used for the main application logic.
- **Spring Boot**: Java framework used to create stand-alone, production-grade Spring-based applications.
- **Maven**: Build automation tool used for managing the Java project's dependencies and build lifecycle.
- **Lucene**: Used for indexing and searching the scientific papers.

## Project Structure
- `src/main/java`: Contains the Java source code.
- `src/main/resources`: Contains the resources for the Java application.
- `src/test/java`: Contains the Java test cases.
- `python_scripts`: Contains the Python scripts.
- `pom.xml`: Maven configuration file.
- `requirements.txt`: Python dependencies file.


## Contributing
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Create a new Pull Request.

## License
This project is licensed under the MIT License.