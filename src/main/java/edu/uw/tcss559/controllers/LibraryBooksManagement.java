package edu.uw.tcss559.controllers;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class LibraryBooksManagement extends Application {

    @Override
    public Set<Class<?>> getClasses() {
		final HashSet<Class<?>> h = new HashSet<>();
		
		// Services that use database
		h.add(BooksREST.class);
		h.add(BookTransactionsREST.class);
		h.add(MembersREST.class);
		h.add(RackRest.class);
		
		// Services that use external service providers
		h.add(NotificationREST.class);
		
		// Services that use MCDA models
		h.add(TopsisREST.class);
		h.add(CriticREST.class);
		
		// Composite services
		h.add(ProfileManagementREST.class);
		
		// Cors
		h.add(CorsFilter.class);

		return h;
    }

}
