package cd.pipeline.demo;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Zählt Aufrufe in Postgres. Nur mit Profil "db" (docker compose) aktiv,
 * ohne Datenbank antwortet der Endpunkt mit 503.
 */
@RestController
public class BesucheController {

	private final ObjectProvider<JdbcTemplate> jdbc;

	public BesucheController(ObjectProvider<JdbcTemplate> jdbc) {
		this.jdbc = jdbc;
	}

	@GetMapping("/besuche")
	public ResponseEntity<String> besuche() {
		JdbcTemplate db = jdbc.getIfAvailable();
		if (db == null) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body("Keine Datenbank konfiguriert (Profil 'db' nicht aktiv)");
		}
		db.execute("CREATE TABLE IF NOT EXISTS besuche (id SERIAL PRIMARY KEY, zeit TIMESTAMP DEFAULT now())");
		db.update("INSERT INTO besuche DEFAULT VALUES");
		Long anzahl = db.queryForObject("SELECT count(*) FROM besuche", Long.class);
		return ResponseEntity.ok("Besuche laut Postgres: " + anzahl);
	}
}
