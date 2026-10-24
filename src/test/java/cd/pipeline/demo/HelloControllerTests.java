package cd.pipeline.demo;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTests {

	@Autowired
	private MockMvc mvc;

	@Test
	void indexLiefertBegruessung() throws Exception {
		mvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Greetings from")));
	}

	@Test
	void somethingLiefertAllOk() throws Exception {
		mvc.perform(get("/something"))
				.andExpect(status().isOk())
				.andExpect(content().string("All Ok"));
	}

	@Test
	void healthIstOhneDatenbankUp() throws Exception {
		mvc.perform(get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void besucheOhneDatenbankLiefert503() throws Exception {
		mvc.perform(get("/besuche"))
				.andExpect(status().isServiceUnavailable());
	}
}
