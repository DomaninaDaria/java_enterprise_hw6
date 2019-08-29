package java_dz_4;


        import lombok.AllArgsConstructor;
        import lombok.Data;
        import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    private Integer id;
    private String name;
    private String specialization;
}