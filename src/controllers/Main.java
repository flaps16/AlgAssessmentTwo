package controllers;

import java.io.File;
import java.util.Collection;
import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import edu.princeton.cs.introcs.In;
import models.Movie;
import models.Rating;
import models.User;
import utils.JSONSerializer;
import utils.Serializer;

public class Main 
{
	private RecommenderAPI reccApi;
	static RecommenderAPI recc = new RecommenderAPI(new JSONSerializer(new File("datastore.json")));
	public Main() throws Exception
	{
		File datastore = new File("datastore.json");
		Serializer serializer = new JSONSerializer(datastore);

		reccApi = new RecommenderAPI(serializer);
		if (datastore.isFile())
		{
			reccApi.load();
		}
	}


	public static void main(String[] args) throws Exception 
	{

		//Start Imports - methods later in class.


/*		usersRead();
		moviesRead();
		ratingsRead();
		recc.store();*/


		Main main = new Main();

		Shell shell = ShellFactory.createConsoleShell("reccApi", "Welcome to recommender-console - ?help for instructions", main);
		shell.commandLoop();

		main.reccApi.store();



	}

	@Command(description="Get all users details")
	public void getUsers ()
	{
		Collection<User> users = reccApi.getUsers();
		System.out.println(users);
	}

	@Command(description="Create a new User")
	public void createUser (@Param(name="first name") String firstName, 	@Param(name="last name") String lastName, 
			@Param(name="age")      	int age,     		@Param(name="gender")  String gender,
			@Param(name="Occupation") String occupation)
	{
		reccApi.addUser(firstName, lastName, age, gender,occupation);
	}

	@Command(description="Get a user")
	public void getUser(@Param(name="User Id") int id) 
	{
		reccApi.getUser(id);
	}
	
	@Command(description="Delete a user")
	public void DeleteUser(@Param(name="User Id") int id) 
	{
		reccApi.removeUser(id);
	}

	@Command(description="Get all movie details")
	public void getMovies ()
	{
		Collection<Movie> movies = reccApi.getMovies();
		System.out.println(movies);
	}
	
	@Command(description="Create a new Movie")
	public void createMovie (@Param(name="Title") String title, 	@Param(name="Year") int year, 
			@Param(name="Imdb Link")      	String imdbLink)
	{
		reccApi.addMovie(title, year, imdbLink);
	}

	@Command(description="Get a Movie")
	public void getMovie(@Param(name="Movie Id") int id) 
	{
		reccApi.getMovie(id);
	}
	
	@Command(description="Delete a Movie")
	public void DeleteMovie(@Param(name="User Id") int id) 
	{
		reccApi.removeMovie(id);
	}
	
	
	@Command(description="Get all rating details")
	public void getRatings ()
	{
		Collection<Rating> ratings = reccApi.getRatings();
		System.out.println(ratings);
	}
	
	@Command(description="Create a new Rating")
	public void createRating (@Param(name="User Id") int userId, 	@Param(name="Movie Id") int movieId, 
			@Param(name="Rating")      	int rating) throws Exception
	{
		reccApi.addRating(userId, movieId, rating);
	}
	
	

	@Command(description="Get top ten movies")
	public void getTopTen ()
	{
		Collection<Movie> topTen = reccApi.getTopTen();
		System.out.println(topTen);
	}

	private static boolean convertToBool(int parseInt) {
		if (parseInt == 1) {
			return true;
		}
		return false;
	}

	public static void usersRead() throws Exception
	{
		File usersFile = new File("DataSmall/users5.dat");
		In inUsers = new In(usersFile);
		//each field is separated(delimited) by a '|'
		String delims = "[|]";
		while (!inUsers.isEmpty()) {
			// get user and rating from data source
			String userDetails = inUsers.readLine();

			// parse user details string
			String[] userTokens = userDetails.split(delims);

			// output user data to console.
			if (userTokens.length == 7) 
			{
				String firstName = userTokens[1];
				String lastName = userTokens[2];
				int age = Integer.parseInt(userTokens[3]);
				String gender = userTokens[4];
				String occupation = userTokens[5];
				int zipCode = Integer.parseInt(userTokens[6]);
				if (firstName.isEmpty() || lastName.isEmpty()) 
				{
					throw new Exception("Invalid member name: " + firstName + ", " + lastName);
				}
				if (age < 0) 
				{
					throw new Exception("Invalid member age: "+age);
				}
				//				if (gender != "M" || gender != "F") 
				//				{
				//					throw new Exception("Invalid member gender: "+gender);
				//				}
				int length = (int)(Math.log10(zipCode)+1);
				if (length != 5) 
				{
					throw new Exception("Invalid member zip: "+zipCode);
				}


				User user = new User(firstName, lastName, age, gender, occupation, zipCode);
				recc.addUser(user);


			}else
			{
				throw new Exception("Invalid member length: "+userTokens.length);
			}
		}
	}

	public static void ratingsRead() throws Exception
	{
		File ratingFile = new File("DataSmall/ratings5.dat");
		In inRatings = new In(ratingFile);
		//each field is separated(delimited) by a '|'
		String delims = "[|]";
		while (!inRatings.isEmpty()) {
			// get user and rating from data source
			String ratingDetails = inRatings.readLine();

			// parse user details string
			String[] ratingTokens = ratingDetails.split(delims);

			// output user data to console.
			if (ratingTokens.length == 4) 
			{

				int userId = Integer.parseInt(ratingTokens[0]);
				int movieId = Integer.parseInt(ratingTokens[1]);
				int rating = Integer.parseInt(ratingTokens[2]);
				//Date timestamp = Date(ratingTokens[3]);
				if (rating < 0 || rating > 5) 
				{
					throw new Exception("Invalid rating: " + rating);
				}
				User user = recc.getUser(userId);
				if (user == null) 
				{
					throw new Exception("User does not exist");
				}
				Movie movie = recc.getMovie(movieId);
				if (movie == null)
				{
					throw new Exception("Movie does not exist");	
				}

				recc.addRating(userId,movieId,rating);


			}else
			{
				throw new Exception("Invalid rating length: "+ratingTokens.length);
			}
		}
	}

	public static void moviesRead() throws Exception
	{
		File moviesFile = new File("DataSmall/items5.dat");
		In inMovies = new In(moviesFile);
		//each field is separated(delimited) by a '|'
		String delims = "[|]";
		while (!inMovies.isEmpty()) {
			// get user and rating from data source
			String movieDetails = inMovies.readLine();

			// parse user details string
			String[] movieTokens = movieDetails.split(delims);

			// output user data to console.
			if (movieTokens.length == 23) 
			{
				int movieId = Integer.parseInt(movieTokens[0]);
				String title = movieTokens[1];
				//Date releaseDate = movieTokens[2];
				String url = movieTokens[3];
				boolean[] genres =
					{
							convertToBool(Integer.parseInt(movieTokens[4])),
							convertToBool(Integer.parseInt(movieTokens[5])),
							convertToBool(Integer.parseInt(movieTokens[6])),
							convertToBool(Integer.parseInt(movieTokens[7])),
							convertToBool(Integer.parseInt(movieTokens[8])),
							convertToBool(Integer.parseInt(movieTokens[9])),
							convertToBool(Integer.parseInt(movieTokens[10])),
							convertToBool(Integer.parseInt(movieTokens[11])),
							convertToBool(Integer.parseInt(movieTokens[12])),
							convertToBool(Integer.parseInt(movieTokens[13])),
							convertToBool(Integer.parseInt(movieTokens[14])),
							convertToBool(Integer.parseInt(movieTokens[15])),
							convertToBool(Integer.parseInt(movieTokens[16])),
							convertToBool(Integer.parseInt(movieTokens[17])),
							convertToBool(Integer.parseInt(movieTokens[18])),
							convertToBool(Integer.parseInt(movieTokens[19])),
							convertToBool(Integer.parseInt(movieTokens[20])),
							convertToBool(Integer.parseInt(movieTokens[21]))
					};

				if (title.isEmpty()) 
				{
					throw new Exception("Invalid title: " + title);
				}


				Movie movie = new Movie(movieId,title,url,genres);
				recc.addMovie(movie);


			}else
			{
				throw new Exception("Invalid member length: "+movieTokens.length);
			}
		}
	}


}