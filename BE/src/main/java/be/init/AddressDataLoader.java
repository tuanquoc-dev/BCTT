package be.init;

import be.entity.District;
import be.entity.Province;
import be.entity.Ward;
import be.repository.DistrictRepository;
import be.repository.ProvinceRepository;
import be.repository.WardRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Transactional
public class AddressDataLoader implements CommandLineRunner {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    public AddressDataLoader(
            ProvinceRepository provinceRepository,
            DistrictRepository districtRepository,
            WardRepository wardRepository
    ) {
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // nếu đã có data thì bỏ qua
        if (provinceRepository.count() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        InputStream is = getClass()
                .getResourceAsStream("/data/vietnam-address.json");

        JsonNode provinces = mapper.readTree(is);

        for (JsonNode p : provinces) {

            Province province = new Province();

            province.setId(
                    p.get("code").asInt()
            );

            province.setName(
                    p.get("name").asText()
            );

            provinceRepository.save(province);

            System.out.println(
                    "SAVE PROVINCE: "
                            + province.getId()
                            + " - "
                            + province.getName()
            );

            // districts
            for (JsonNode d : p.get("districts")) {

                District district = new District();

                district.setId(
                        d.get("code").asInt()
                );

                district.setName(
                        d.get("name").asText()
                );

                district.setProvince(province);

                districtRepository.save(district);

                System.out.println(
                        "SAVE DISTRICT: "
                                + district.getId()
                                + " - "
                                + district.getName()
                );

                // wards
                for (JsonNode w : d.get("wards")) {

                    Ward ward = new Ward();

                    ward.setId(
                            w.get("code").asInt()
                    );

                    ward.setName(
                            w.get("name").asText()
                    );

                    ward.setDistrict(district);

                    wardRepository.save(ward);


                }
            }
        }

        System.out.println("IMPORT ADDRESS SUCCESS");
    }
}