package uz.napa.clinic.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.napa.clinic.entity.*;
import uz.napa.clinic.entity.enums.UserStatus;
import uz.napa.clinic.repository.*;
import uz.napa.clinic.service.iml.helper.SmsSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Component
public class DataLoader implements CommandLineRunner {
        @Value("${spring.datasource.initialization-mode}")
    private String initialMode;
    final
    UserRepository userRepository;

    final
    PasswordEncoder passwordEncoder;

    final
    RoleRepository roleRepository;

    final
    PermissionRepository permissionRepository;
    final
    RegionRepository regionRepository;
    final
    PositionRepository positionRepository;
    final
    DistrictRepository districtRepository;
    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    WordsRepository wordsRepository;

    @Autowired
    LangRepository langRepository;
//    final
//    AttachmentTypeRepository attachmentTypeRepository;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, PermissionRepository permissionRepository, RegionRepository regionRepository, PositionRepository positionRepository, DistrictRepository districtRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
//        this.attachmentTypeRepository = attachmentTypeRepository;
        this.permissionRepository = permissionRepository;
        this.regionRepository = regionRepository;
        this.positionRepository = positionRepository;
        this.districtRepository = districtRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (initialMode.equals("always")) {
            Permission reader = new Permission(1L, "READ_PERMISSION", "READ PERMISSION");
            Permission writer = new Permission(2L, "WRITE_PERMISSION", "WRITE PERMISSION");
            Permission updater = new Permission(3L, "UPDATE_PERMISSION", "UPDATE PERMISSION");
            Permission deleter = new Permission(4L, "DELETE_PERMISSION", "DELETE PERMISSION");
            List<Permission> permissionEntityList = new ArrayList<>(Arrays.asList(reader, writer, updater, deleter));
            permissionRepository.saveAll(permissionEntityList);

            Role admin = new Role("ADMIN", "ADMIN", "Tizimdagi barcha huquqlarga ega bo'lgan role", true);
            Role user = new Role("USER", "USER", "Tizimdagi cheklangan huquqlarga ega bo'lgan role", true);
            Role moderator = new Role("MODERATOR", "MODERATOR", "Tizimdagi cheklangan  huquqlarga ega bo'lgan role", true);
            Role superModerator = new Role("SUPER_MODERATOR_AND_MODERATOR", "SUPER_MODERATOR_AND_MODERATOR", "Tizimdagi barcha huqularga ega", true);
            Role superModeratorAndModerator = new Role("SUPER_MODERATOR", "SUPER_MODERATOR", "Tizimdagi barcha huqularga ega", true);
            Role listener = new Role("LISTENER", "LISTENER", "Tizimdagi cheklangan  huqularga ega", true);
            admin.setPermissions(Arrays.asList(reader, writer, updater));
            user.setPermissions(Arrays.asList(reader, writer, updater));
            moderator.setPermissions(Arrays.asList(reader, writer, updater, deleter));
            superModerator.setPermissions(Arrays.asList(reader, writer, updater, deleter));
            listener.setPermissions(Arrays.asList(reader, writer, updater, deleter));
            roleRepository.saveAll(Arrays.asList(admin, user, moderator, superModerator, listener,superModeratorAndModerator));
            User user1 = new User(
                    "Clinic Admin",
                    new Date(99, 10, 2),
                    positionRepository.getOne(1L),
                    1,
                    districtRepository.getOne(196L),
                    "Toshken sh. Olmazor 2",
                    sectionRepository.getOne(1L),
                    "admin",
                    "clinic_admin@gmail.com",
                    "erkak",
                    UserStatus.ADMIN,
                    passwordEncoder.encode("admin123"),
                    roleRepository.findAll()
            );
            user1.setViewed(true);
            userRepository.save(
                    user1
                    );



            List<Words> wordsList=new ArrayList<>();
            wordsList.add(new Words(langRepository.save(
                    new Lang(
                            "O'zbekiston Respublikasining Konstitutsiyasi",
                            "Ўзбекистон Республикасининг Конституцияси",
                            "Конституция Республики Узбекистан",
                            "Ўзбекистон Республикасининг Конституцияси"
                    )
            ),langRepository.save(new Lang(
                    "https://lex.uz/docs/-20596",
                    "https://lex.uz/docs/20596",
                    "https://lex.uz/docs/35869",
                    "https://lex.uz/docs/20596"
            ))));
            wordsList.add(new Words(langRepository.save(
                    new Lang(
                            "O'zbekiston Respublikasining «Prokuratura to'g'risida»gi Qonuni",
                            "Ўзбекистон Республикасининг «Прокуратура тўғрисида»ги Қонуни",
                            "Закон Республики Узбекистан",
                            "Ўзбекистон Республикасининг «Прокуратура тўғрисида»ги Қонуни"
                    )
            ),langRepository.save(new Lang(
                    "https://lex.uz/docs/-106197",
                    "https://lex.uz/docs/106197",
                    "https://lex.uz/docs/105533",
                    "https://lex.uz/docs/106197"
            ))));
            wordsList.add(new Words(langRepository.save(
                    new Lang(
                            "O'zbekiston Respublikasining «O'zbekiston Respublikasi prokuratura organlari xodimlari  kunini belgilash to'g'risida»gi Qonuni",
                            "Ўзбекистон Республикасининг «Ўзбекистон Республикаси прокуратура органлари ходимлари кунини белгилаш тўғрисида»ги Қонуни",
                            "Закон Республики Узбекистан «Об установлении дня работников органов прокуратуры Республики Узбекистан»",
                            "Ўзбекистон Республикасининг «Ўзбекистон Республикаси прокуратура органлари ходимлари кунини белгилаш тўғрисида»ги Қонуни"
                    )
            ),langRepository.save(new Lang(
                    "https://lex.uz/docs/-3080268",
                    "https://lex.uz/docs/3080268",
                    "https://lex.uz/docs/3080266",
                    "https://lex.uz/docs/3080268"
            ))));
            wordsList.add(new Words(langRepository.save(
                    new Lang(
                            "O'zbekiston Respublikasi Prezidentining «O'zbekiston Respublikasi prokuraturasi organlari  to'g'risida»gi Farmoni",
                            "Ўзбекистон Республикаси Президентининг «Ўзбекистон Республикаси прокуратураси органлари тўғрисида»ги Фармони",
                            "Указ Президента Республики Узбекистан «Об органах прокуратуры Республики Узбекистан»",
                            "Ўзбекистон Республикаси Президентининг «Ўзбекистон Республикаси прокуратураси органлари тўғрисида»ги Фармони"
                    )
            ),langRepository.save(new Lang(
                    "https://lex.uz/docs/-147159",
                    "https://lex.uz/docs/147159",
                    "https://lex.uz/docs/147166",
                    "https://lex.uz/docs/147159"
            ))));
            wordsList.add(new Words(langRepository.save(
                    new Lang(
                            "O'zbekiston Respublikasi Prezidentining «Prokuratura organlari kadrlarini tayyorlash, qayta tayyorlash va ularning malakasini oshirish tizimini tubdan takomillashtirish chora-tadbirlari  to'g'risida»gi Farmoni",
                            "Ўзбекистон Республикаси Президентининг «Прокуратура органлари кадрларини тайёрлаш, қайта тайёрлаш ва уларнинг малакасини ошириш тизимини тубдан такомиллаштириш чора-тадбирлари тўғрисида»ги Фармони",
                            "Указ Президента Республики Узбекистан «О мерах по коренному совершенствованию системы подготовки, переподготовки и повышения квалификации кадров органов прокуратуры»",
                            "Ўзбекистон Республикаси Президентининг «Прокуратура органлари кадрларини тайёрлаш, қайта тайёрлаш ва уларнинг малакасини ошириш тизимини тубдан такомиллаштириш чора-тадбирлари тўғрисида»ги Фармони"
                    )
            ),langRepository.save(new Lang(
                    "https://www.lex.uz/docs/3727063",
                    "https://www.lex.uz/docs/3727063",
                    "https://www.lex.uz/docs/3727138",
                    "https://www.lex.uz/docs/3727063"
            ))));
            wordsList.add(new Words(langRepository.save(
                    new Lang(
                            "O'zbekiston Respublikasi Prezidentining «Huquqbuzarliklar profilaktikasi va jinoyatchilikka  qarshi kurashish tizimini yanada takomillashtirish chora-tadbirlari to'g'risida»gi Qarori",
                            "Ўзбекистон Республикаси Президентининг «Ҳуқуқбузарликлар профилактикаси ва жиноятчиликка қарши курашиш тизимини янада такомиллаштириш чора-тадбирлари тўғрисида»ги Қарори",
                            "Постановление Президента Республики Узбекистан «О мерах по дальнейшему совершенствованию системы профилактики правонарушений и борьбы с преступностью»",
                            "Ўзбекистон Республикаси Президентининг «Ҳуқуқбузарликлар профилактикаси ва жиноятчиликка қарши курашиш тизимини янада такомиллаштириш чора-тадбирлари тўғрисида»ги Қарори"
                    )
            ),langRepository.save(new Lang(
                    "https://lex.uz/docs/3141186",
                    "https://lex.uz/docs/3141186",
                    "https://lex.uz/docs/3141184",
                    "https://lex.uz/docs/3141186"
            ))));
            wordsList.add(new Words(langRepository.save(
                    new Lang(
                            "O'zbekiston Respublikasi Prezidentining «Ijtimoiy-iqtisodiy islohotlarni amalga oshirish,  mamlakatni modernizatsiya qilish, inson huquq va erkinliklarining ishonchli himoyasini  ta'minlashda prokuratura organlarining rolini kuchaytirish to'g'risida»gi Farmoni   ",
                            "Ўзбекистон Республикаси Президентининг «Ижтимоий-иқтисодий ислоҳотларни амалга ошириш, мамлакатни модернизация қилиш, инсон ҳуқуқ ва эркинликларининг ишончли ҳимоясини таъминлашда прокуратура органларининг ролини кучайтириш тўғрисида»ги Фармони   ",
                            "Указ Президента Республики Узбекистан «Об усилении роли органов прокуратуры в реализации социально-экономических реформ и модернизации страны, обеспечении надежной защиты прав и свобод человека»",
                            "Ўзбекистон Республикаси Президентининг «Ижтимоий-иқтисодий ислоҳотларни амалга ошириш, мамлакатни модернизация қилиш, инсон ҳуқуқ ва эркинликларининг ишончли ҳимоясини таъминлашда прокуратура органларининг ролини кучайтириш тўғрисида»ги Фармони   "
                    )
            ),langRepository.save(new Lang(
                    "https://www.lex.uz/docs/3177796",
                    "https://www.lex.uz/docs/3177796",
                    "https://www.lex.uz/docs/3177798",
                    "https://www.lex.uz/docs/3177796"
            ))));
            wordsList.add(new Words(langRepository.save(
                    new Lang(
                            "O'zbekiston Respublikasi Prezidentining «Jinoiy-huquqiy statistika tizimini tubdan  takomillashtirish va jinoyatlarni tizimli tahlil qilish samaradorligini oshirish chora-tadbirlari  to'g'risida» Farmoni",
                            "Ўзбекистон Республикаси Президентининг «Жиноий-ҳуқуқий статистика тизимини тубдан такомиллаштириш ва жиноятларни тизимли таҳлил қилиш самарадорлигини ошириш чора-тадбирлари тўғрисида» Фармони",
                            "Указ Президента Республики Узбекистан «О мерах по коренному совершенствованию системы уголовно-правовой статистики и повышению эффективности системного анализа преступлений»",
                            "Ўзбекистон Республикаси Президентининг «Жиноий-ҳуқуқий статистика тизимини тубдан такомиллаштириш ва жиноятларни тизимли таҳлил қилиш самарадорлигини ошириш чора-тадбирлари тўғрисида» Фармони"
                    )
            ),langRepository.save(new Lang(
                    "https://lex.uz/ru/docs/-4034449",
                    "https://lex.uz/ru/docs/4034449",
                    "https://lex.uz/ru/docs/4034455",
                    "https://lex.uz/ru/docs/4034449"
            ))));

            wordsRepository.saveAll(wordsList);


//            attachmentTypeRepository.save(new AttachmentType("image/jpeg"))
//
        }
    }
}

