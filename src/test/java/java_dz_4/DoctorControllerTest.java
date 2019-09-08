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
        doctorRepo.deleteAll();

    }


    @Test
    public void shouldFindAllDoctors() throws Exception {
        doctorRepo.save(new Doctor(null, "Kirill", "doctor1"));
        doctorRepo.save(new Doctor(null, "Vasya", "doctor2"));
        doctorRepo.save(new Doctor(null, "Nikita", "doctor3"));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(fromResource("all-doctors.json"), false))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Kirill")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialization", Matchers.is("doctor1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is("Vasya")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].specialization", Matchers.is("doctor2")));
    }

    @Test
    public void shouldReturnKirill() throws Exception {
        doctorRepo.save(new Doctor(null, "Kirill", "doctor1"));
        doctorRepo.save(new Doctor(null, "Vasya", "doctor2"));
        doctorRepo.save(new Doctor(null, "Nikita", "doctor3"));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors")
                .param("specializations", "doctor1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialization", Matchers.is("doctor1")));
    }

    @Test
    public void shouldReturnVasya() throws Exception {
        doctorRepo.save(new Doctor(null, "Kirill", "doctor1"));
        doctorRepo.save(new Doctor(null, "Vasya", "doctor2"));
        doctorRepo.save(new Doctor(null, "Nikita", "doctor3"));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors")
                .param("name", "Vasya")
                .param("specializations", "doctor2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialization", Matchers.is("doctor2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Vasya")));
    }

    @Test
    public void shouldUpdateKirill() throws Exception {
        Integer id = doctorRepo.save(new Doctor(null, "Kirill", "doctor1")).getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/{id}", id)
                .contentType("application/json")
                .content(fromResource("update-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertThat(doctorRepo.findById(id).get().getName()).isEqualTo("Roma");
        Assertions.assertThat(doctorRepo.findById(id).get().getSpecialization()).isEqualTo("doctor3");
    }


    @Test
    public void shouldReturnNOtFoundSpecializationForCreateDoctor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                .contentType("application/json")
                .content(fromResource("create-update-wrong-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturnNOtFoundSpecializationForUpdateDoctor() throws Exception {
        Integer id = doctorRepo.save(new Doctor(null, "Kirill", "doctor1")).getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/{id}", id)
                .contentType("application/json")
                .content(fromResource("create-update-wrong-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    public void shouldReturnNotFoundForDelete() throws Exception {
        doctorRepo.save(new Doctor(null, "Kirill", "doctor1"));
        doctorRepo.save(new Doctor(null, "Vasya", "doctor2"));
        doctorRepo.save(new Doctor(null, "Nikita", "doctor3"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/{id}", 4))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldDeleteNikita() throws Exception {
        doctorRepo.save(new Doctor(null, "Kirill", "doctor1"));
        doctorRepo.save(new Doctor(null, "Vasya", "doctor2"));
        Integer id = doctorRepo.save(new Doctor(null, "Nikita", "doctor3")).getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertThat(doctorRepo.findById(3)).isEmpty();
    }

    @Test
    public void shouldCreateDoctor() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                .contentType("application/json")
                .content(fromResource("create-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(header().string("location", containsString("http://my-doctor.com/doctors/")))
                .andReturn().getResponse();
        Integer id = Integer.parseInt(response.getHeader("location")
                .replace("http://my-doctor.com/doctors/", ""));
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