package java_dz_4;


import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    DoctorRepo doctorRepo;

    @After
    public void cleanUp() {
        doctorRepo.cleanAll();

    }


    @Test
    public void shouldFindAllDoctors() throws Exception {
        doctorRepo.createDoctor(new Doctor(null, "Kirill", "sp1"));
        doctorRepo.createDoctor(new Doctor(null, "Vasya", "sp2"));
        doctorRepo.createDoctor(new Doctor(null, "Nikita", "sp3"));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(fromResource("all-doctors.json"), false))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Kirill")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialization", Matchers.is("sp1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is("Vasya")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].specialization", Matchers.is("sp2")));
    }

    @Test
    public void shouldReturnKirill() throws Exception {
        doctorRepo.createDoctor(new Doctor(null, "Kirill", "sp1"));
        doctorRepo.createDoctor(new Doctor(null, "Vasya", "sp2"));
        doctorRepo.createDoctor(new Doctor(null, "Nikita", "sp3"));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors")
                .param("specialization", "sp1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialization", Matchers.is("sp1")));
    }

    @Test
    public void shouldReturnVasya() throws Exception {
        doctorRepo.createDoctor(new Doctor(null, "Kirill", "sp1"));
        doctorRepo.createDoctor(new Doctor(null, "Vasya", "sp2"));
        doctorRepo.createDoctor(new Doctor(null, "Nikita", "sp3"));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors")
                .param("name", "V")
                .param("specialization", "sp2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialization", Matchers.is("sp2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Vasya")));
    }

    @Test
    public void shouldUpdateKirill() throws Exception {
        Integer id = doctorRepo.createDoctor(new Doctor(null, "Kirill", "sp1")).getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/{id}", id)
                .contentType("application/json")
                .content(fromResource("update-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertThat(doctorRepo.findById(id).get().getName()).isEqualTo("Roma");
        Assertions.assertThat(doctorRepo.findById(id).get().getSpecialization()).isEqualTo("sp4");
    }

    @Test
    public void shouldReturnNotFoundForUpdate() throws Exception {
        Integer id = doctorRepo.createDoctor(new Doctor(null, "Kirill", "sp1")).getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/{id}", id+1)
                .contentType("application/json")
                .content(fromResource("update-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    public void shouldReturnNotFoundForDelete() throws Exception {
        doctorRepo.createDoctor(new Doctor(null, "Kirill", "sp1"));
        doctorRepo.createDoctor(new Doctor(null, "Vasya", "sp2"));
        doctorRepo.createDoctor(new Doctor(null, "Nikita", "sp3"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/{id}", 4))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldDeleteNikita() throws Exception {
        doctorRepo.createDoctor(new Doctor(null, "Kirill", "sp1"));
        doctorRepo.createDoctor(new Doctor(null, "Vasya", "sp2"));
        doctorRepo.createDoctor(new Doctor(null, "Nikita", "sp3"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/{id}", 3))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertThat(doctorRepo.findById(3)).isEmpty();
    }

    @Test
    public void shouldCreateDoctor() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                .contentType("application/json")
                .content(fromResource("create-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(header().string("location", containsString("http://localhost:8080/doctors/")))
                .andReturn().getResponse();
        Integer id = Integer.parseInt(response.getHeader("location")
                .replace("http://localhost:8080/doctors/", ""));
        Assertions.assertThat(doctorRepo.findById(id)).isPresent();
    }

    public String fromResource(String path) {
        try {
            File file = ResourceUtils.getFile("classpath:" + path);
            return Files.readString(file.toPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}