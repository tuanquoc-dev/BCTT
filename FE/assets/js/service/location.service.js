const LocationService = {

    data: [],

    async init() {

        const res = await fetch(
            "../../assets/data/vietnam-address.json"
        );

        this.data = await res.json();
    },

    getProvinces() {

        return this.data;
    },

    getDistricts(provinceCode) {

        const province = this.data.find(
            p => Number(p.code) === Number(provinceCode)
        );


        return province?.districts || [];
    },

    getWards(provinceCode, districtCode) {

        const districts =
            this.getDistricts(provinceCode);

        const district = districts.find(
            d => Number(d.code) === Number(districtCode)
        );


        return district?.wards || [];
    }
};