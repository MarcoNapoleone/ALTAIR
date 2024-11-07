package it.uniroma3.idd.HW_2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
@Transactional
public class ArticleService {

 	public Document getArticle(Long id) {

	}

 	public Document getArticleQuery(String query) {
		//TODO LUCENE
	}

	public Collection<Document> getAllRestaurants() {
		Collection<Restaurant> restaurants = restaurantRepository.findAll();
		return restaurants;
	}

	public Collection<Document> getAllRestaurantsByLocation(String location) {
		Collection<Restaurant> restaurants = restaurantRepository.findAllByLocation(location);
		return restaurants;
	}


}
