import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 *
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
		try {
			// Encode special characters in movie title
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){
			return null;
		}

		// Initialize connection in case of connection errors
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		String[] movies = null;

		try {

			URL url = new URL(urlString); // Constructors url throws error if malformed

			urlConnection = (HttpURLConnection) url.openConnection(); // call on parent of HttpURLConnection
			urlConnection.setRequestMethod("GET"); // Set GET or POST
			urlConnection.connect(); // Create a connection to the url => this is where many errors can happen

			// Input Stream allows us to read in the url as bytes throught the buffer reader line by line
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer(); // allows us to concat the entire file in memory
			if (inputStream == null) { // if there is no content return null
				return null;
			}
			// Iterable reader that reads in characters, arrays, and lines
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line = reader.readLine(); // read in first line
			while (line != null) { // iterate through entire url input stream object
				buffer.append(line + "\n");
				line = reader.readLine();
			}

			if (buffer.length() == 0) {
				return null;
			}
			String results = buffer.toString(); // creates a searchable string
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			movies = results.split("\n"); // parse each movie into an array
		}
		catch (IOException e) {
			return null;
		}
		finally {
			if (urlConnection != null) {
				urlConnection.disconnect(); // close connection
			}
			if (reader != null) {
				try {
					reader.close(); // close buffer reader
				}
				catch (IOException e) {
				}
			}
		}

		return movies;
	}


	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		while(searching) {
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
