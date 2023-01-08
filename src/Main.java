
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public class Main {
    public static final int BOOK_HEAP_BOOK_COUNT = 20;
    public static final Pair<Double, WeightUnit> KARATUK_ARMOR_PIERCING_DEGREE = new Pair<>(48.0, WeightUnit.POUND);
    public static final int NUTCRACKER_STEPS_WITH_NUT_CORE_NUMBER = 7;
    public static final String PRINCESS_BEAUTY_REGAIN_CONDITION = "The beauty of the princess will return to her again " +
            "if the Krakatuk nut is found and the princess is given to eat its delicious kernel";
    public static final int STAR_COUNT = 100;

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        HeroFactory heroFactory = new HeroFactory();

        Hero astronomer = heroFactory.createHero(HeroType.ASTRONOMER);
        Hero drosselmaster = heroFactory.createHero(HeroType.DROSSELMASTER);
        Hero princess = heroFactory.createHero(HeroType.PRINCESS);
        Hero karatuk = heroFactory.createHero(HeroType.KARATUK);

        astronomer.addFriends(new PersonFriend(drosselmaster, PersonFriendType.BOSOM));
        drosselmaster.addFriends(new PersonFriend(astronomer, PersonFriendType.BOSOM));

        // Horoscope creating part
        HeroAction askAction = new AskAction(drosselmaster, new DialogAction(drosselmaster, astronomer));
        drosselmaster.doAction(askAction);

        if (astronomer.containsFriends(drosselmaster) && drosselmaster.containsFriends(astronomer)) {
            HeroAction cryingHugAction = new CryingActionProxy(new HugAction(drosselmaster, astronomer), drosselmaster, astronomer);
            drosselmaster.doAction(cryingHugAction);
        }

        new CloseDownAction(astronomer, drosselmaster).run();

        BookSubject[] bookSubjects = {BookSubject.SYMPATHIES, BookSubject.ANTIPATHIES,
                BookSubject.INSTINCT, BookSubject.ETC_WISE_THINGS};
        new BookLearnActionUntilNight(getBookHeap(BOOK_HEAP_BOOK_COUNT, bookSubjects), astronomer, drosselmaster).run();

        List<Star> stars = getStars(STAR_COUNT);
        astronomer.doAction(new TelescopePointedAction(astronomer, stars));

        HoroscopeResult result = new HoroscopeResult();
        new HoroscopeCreatingAction(result, (Nut) karatuk, astronomer, drosselmaster).run();
        new HoroscopeResultReadingAction(result, astronomer, drosselmaster).run();

//        Он тотчас же просил позволения переговорить с придворным астрономом,
//        и был сведен к нему под стражей. Оба обнялись в слезах, потому что были закадычными друзьями,
//        а затем, запершись в уединенном кабинете, начали рыться в груде книг, трактующих об инстинкте,
//        симпатиях, антипатиях и многих тому подобных, премудрых вещах.
//                С наступлением ночи астроном навел на звезды телескоп
//        и затем составил с помощью понимающего в этом деле толк Дроссельмейера гороскоп принцессы.
//                Работа эта оказалась очень трудной. Линии перепутывались до такой степени,
//                что только после долгого, упорного труда, оба с восторгом прочли совершенно ясное
//    предопределение, что красота принцессы вернется к ней снова, если будет найден орех Кракатук
//        и принцессе дадут скушать его вкусное ядрышко.

        Nutcracker nutcracker = (Nutcracker) heroFactory.createHero(HeroType.NUTCRACKER);
        Optional.ofNullable(karatuk).map(nut -> (Nut) nut)
                .filter(nut -> nutcracker.isNeverShaved() && !nutcracker.isWearBoots())
                .map(peek(nut -> nutcracker.doAction(new SingleHeroAction(nutcracker) {
                    @Override
                    public void run() {
                        nut.crack();
                    }
                })))
                .map(peek(nut -> {
                    GiveNutCoreAction giveNutCoreAction = new GiveNutCoreAction(nut.core, nutcracker, princess);
                    ClosedEyesDoingActionsProxy closedEyesDoingActionsProxy = new ClosedEyesDoingActionsProxy(
                            nutcracker,
                            giveNutCoreAction,
                            new FallBackWithoutStumbleAction(nutcracker, NUTCRACKER_STEPS_WITH_NUT_CORE_NUMBER)
                    );
                    nutcracker.doAction(closedEyesDoingActionsProxy);
                }))
                .ifPresent(nut -> princess.doAction(new RegainedPrincessBeautyAction(princess)));
//        Horoscope horoscope = new HoroscopeFactory().createHoroscope(stars);
//        nutcracker.doAction();

//        List<Book> filteredBooks = books.stream().filter(Book::containsSubject).toList();

//        Орех Кракатук имел такую твердую скорлупу, что ее не могло бы пробить сорокавосьмифунтовое пушечное ядро.
//        Мало того — этот орех должен был разгрызть на глазах у принцессы маленький человечек,
//        который еще ни разу не брился и не носил сапогов. Кроме того, человечку необходимо было подать
//        принцессе ядро от разгрызенного ореха с зажмуренными глазами, а затем отступить семь шагов назад,
//        ни разу не споткнувшись, и вновь открыть глаза.
    }

    private static List<Star> getStars(int starCount) {
        List<Star> stars = new ArrayList<>();
        for (int i = 0; i < STAR_COUNT; i++) {
            stars.add(new Star());
        }
        return stars;
    }

    static <T> UnaryOperator<T> peek(Consumer<T> c) {
        return x -> {
            c.accept(x);
            return x;
        };
    }

    private static BookHeap getBookHeap(int bookHeapBookCount, BookSubject[] bookSubjects) {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < bookHeapBookCount; i++) {
            books.add(new Book("Book %s".formatted(Math.random())));
        }
        return new BookHeap(books, bookSubjects);
    }

//    public static class HoroscopeFactory {
//        public Horoscope createHoroscope(Star[] stars) {
//            return new Horoscope(stars);
//        }
//    }

    static class HoroscopeLine {

    }

    private enum BookSubject {
        SYMPATHIES,
        ANTIPATHIES,
        INSTINCT,
        ETC_WISE_THINGS,
        ETC
    }

    private static class Book {
        private final String bookName;

        public Book(String bookName) {
            this.bookName = bookName;
        }
    }

    private static class BookHeap {
        private final BookSubject[] subjects;
        private final List<Book> books;

        BookHeap(List<Book> books, BookSubject... subjects) {
            this.books = books;
            this.subjects = subjects;
        }

        public boolean containsSubject(BookSubject... sub) {
            return Arrays.stream(subjects).anyMatch(v -> {
                for (BookSubject s : sub) {
                    if (s == v) {
                        return true;
                    }
                }
                return false;
            });
        }

        public BookSubject[] getSubjects() {
            return subjects;
        }

        public List<Book> getBooks() {
            return books;
        }
    }

    private static class Star {

    }

    private static class Horoscope {
        private HoroscopeLine[] lines;

        public Horoscope(HoroscopeLine[] lines) {
            this.lines = lines;
        }


    }

    private enum PersonFriendType {
        BOSOM,
        FELLOW
    }

    private static class PersonFriend {
        private final Hero person;
        private PersonFriendType type;

        public PersonFriend(Hero person, PersonFriendType type) {
            this.person = person;
            this.type = type;
        }

        public Hero getPerson() {
            return person;
        }

        public PersonFriendType getType() {
            return type;
        }

        public void setType(PersonFriendType type) {
            this.type = type;
        }
    }

    private static class Pair<T, V> {
        private T first;
        private V second;

        public Pair(T first, V second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public void setFirst(T first) {
            this.first = first;
        }

        public V getSecond() {
            return second;
        }

        public void setSecond(V second) {
            this.second = second;
        }
    }

    private enum WeightUnit {
        KG,
        POUND
    }

    private static class NutCore {
        private final boolean tasty;

        private NutCore(boolean tasty) {
            this.tasty = tasty;
        }

        public boolean isTasty() {
            return tasty;
        }
    }

    private static class NutShell {
        private final Pair<Double, WeightUnit> armorPiercingDegree;

        public NutShell(Pair<Double, WeightUnit> armorPiercingDegree) {
            this.armorPiercingDegree = armorPiercingDegree;
        }

        public Pair<Double, WeightUnit> getArmorPiercingDegree() {
            return armorPiercingDegree;
        }
    }

    private enum NutState {
        NEW,
        EXTRACTED;
    }

    private static class Nut extends Hero {
        private NutState state;
        private final NutCore core;
        private NutShell shell;

        public Nut(String name, Pair<Double, WeightUnit> armorPiercingDegree) {
            super(name);
            this.shell = new NutShell(armorPiercingDegree);
            this.core = new NutCore(true);
            this.state = NutState.NEW;
        }

        public void crack() {
            if (this.state == NutState.EXTRACTED) {
                throw new UnsupportedOperationException("The nut is already extracted");
            }
            this.state = NutState.EXTRACTED;
            this.shell = null;
        }

        public NutState getState() {
            return state;
        }

        public NutCore getCore() {
            return core;
        }

        public NutShell getShell() {
            return shell;
        }
    }

    public static class HeroFactory {
        public Hero createHero(HeroType heroType) {
            switch (heroType) {
                case DROSSELMASTER,
                        ASTRONOMER,
                        PRINCESS -> {
                    return new HumanHero(heroType.heroName);
                }
                case NUTCRACKER -> {
                    return new Nutcracker(true, false);
                }
                case KARATUK -> {
                    return new Nut("Karatuk", KARATUK_ARMOR_PIERCING_DEGREE);
                }
            }
            throw new UnsupportedOperationException("Unknown hero type %s".formatted(heroType.name()));
        }
    }

    private enum HeroType {
        ASTRONOMER("Astronomer"),
        NUTCRACKER("Nutcracker"),
        KARATUK("Karatuk"),
        DROSSELMASTER("Drosselmaster"),
        PRINCESS("Princess");

        private final String heroName;

        HeroType(String heroName) {
            this.heroName = heroName;
        }
    }

    static class Hero {
        private final String name;

        public Hero(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isHuman() {
            return false;
        }

        public void addFriends(PersonFriend... friends) {
            throw new UnsupportedOperationException("Hero can't has friends");
        }

        public boolean containsFriends(Hero... inputFriends) {
            throw new UnsupportedOperationException("Hero can't has friends");
        }

        public void doAction(HeroAction action) {
            if (action.isGroupAction() && !action.isProxy()) {
                throw new UnsupportedOperationException("Can't apply group action for single person");
            }
            action.run();
        }
    }

    static class HumanHero extends Hero {
        private final List<PersonFriend> friends = new ArrayList<>();

        public HumanHero(String name) {
            super(name);
        }

        @Override
        public boolean isHuman() {
            return true;
        }

        public void addFriends(PersonFriend... friends) {
            if (Arrays.stream(friends).anyMatch(friend -> this.equals(friend.person))) {
                throw new UnsupportedOperationException("Can not instantiate yourself as friend");
            }
            Collections.addAll(this.friends, friends);
        }

        public boolean containsFriends(Hero... inputFriends) {
            List<Hero> persons = friends.stream().map(fr -> fr.person).toList();
            return Arrays.stream(inputFriends).allMatch(persons::contains);
        }
    }

    static class Nutcracker extends Hero {
        private boolean neverShaved;
        private boolean wearBoots;

        public Nutcracker(boolean neverShaved, boolean wearBoots) {
            super("Nutcracker");
            this.neverShaved = neverShaved;
            this.wearBoots = wearBoots;
        }

        @Override
        public boolean isHuman() {
            return false;
        }

        public boolean isNeverShaved() {
            return neverShaved;
        }

        public void setNeverShaved(boolean neverShaved) {
            this.neverShaved = neverShaved;
        }

        public boolean isWearBoots() {
            return wearBoots;
        }

        public void setWearBoots(boolean wearBoots) {
            this.wearBoots = wearBoots;
        }
    }

    interface HeroAction extends Runnable {
        boolean isGroupAction();
        default boolean isProxy() {
            return false;
        }
    }

    static abstract class SingleHeroAction implements HeroAction {
        protected final Hero person;

        SingleHeroAction(Hero person) {
            this.person = person;
        }

        @Override
        public boolean isGroupAction() {
            return false;
        }
    }


    static abstract class GroupHeroAction implements HeroAction {
        protected final List<Hero> persons;

        public GroupHeroAction(Hero... persons) {
            this.persons = Arrays.stream(persons).toList();
        }

        @Override
        public boolean isGroupAction() {
            return true;
        }
    }

    static class AskAction extends SingleHeroAction {
        private final HeroAction askAction;

        public AskAction(Hero person, HeroAction askFutureAction) {
            super(person);
            this.askAction = askFutureAction;
        }

        @Override
        public void run() {
            LOGGER.info("%s asks for %s".formatted(person.name, askAction)); // not to do smth, just ask how to do it
        }
    }

    static class FallBackWithoutStumbleAction extends SingleHeroAction {
        private final int stepsNumber;

        public FallBackWithoutStumbleAction(Hero person, int stepsNumber) {
            super(person);
            this.stepsNumber = stepsNumber;
        }

        @Override
        public void run() {
            int currentStep = 0;
            for (int i = 0; i < stepsNumber; i++) {
                ++currentStep;
                LOGGER.info("%s made %d step".formatted(person.name, currentStep));
            }
        }
    }

    static class TelescopePointedAction extends SingleHeroAction {
        private final List<Star> stars;

        public TelescopePointedAction(Hero person, List<Star> stars) {
            super(person);
            this.stars = stars;
        }

        @Override
        public void run() {
            LOGGER.info("%s pointed telescope to stars (star count = %d)".formatted(person.name, stars.size()));
        }
    }

    static class GiveNutCoreAction extends SingleHeroAction {
        private final Hero receiver;
        private final NutCore nutCore;

        public GiveNutCoreAction(NutCore nutCore, Hero person, Hero receiver) {
            super(person);
            this.nutCore = nutCore;
            this.receiver = receiver;
        }

        @Override
        public void run() {
            LOGGER.info("%s gives Karatuk's nut core to %s".formatted(person.name, receiver.name));
        }
    }

    static class NutCoreEatingAction extends SingleHeroAction {
        public NutCoreEatingAction(Hero person) {
            super(person);
        }

        @Override
        public void run() {
            LOGGER.info("Princess eat tasty Karatuk nut core");
        }
    }

    static class RegainedPrincessBeautyAction extends SingleHeroAction {
        public RegainedPrincessBeautyAction(Hero person) {
            super(person);
        }

        @Override
        public void run() {
            LOGGER.info("The princess was regained her lost beauty");
        }
    }

    static class DialogAction extends GroupHeroAction {

        DialogAction(Hero... persons) {
            super(persons);
        }

        @Override
        public void run() {
            LOGGER.info("Dialog process between %s".formatted(PersonUtilityHelper.getPersonNamesByPersons(persons))); // speech between persons
        }

        @Override
        public String toString() {
            return "Dialog Action between %s".formatted(PersonUtilityHelper.getPersonNamesByPersons(persons));
        }
    }

    static class CryingActionProxy extends GroupHeroAction {
        private final HeroAction action;

        public CryingActionProxy(HeroAction action, Hero... crybabies) {
            super(crybabies);
            this.action = action;
        }

        @Override
        public void run() {
            startCrying();
            action.run();
            endCrying();
        }

        @Override
        public boolean isProxy() {
            return true;
        }

        private void startCrying() {
            persons.forEach(person -> LOGGER.info("%s starts crying".formatted(person.name)));
        }

        private void endCrying() {
            persons.forEach(person -> LOGGER.info("%s ends crying".formatted(person.name)));
        }
    }

    static class ClosedEyesDoingActionsProxy extends SingleHeroAction {
        private final HeroAction[] actions;

        public ClosedEyesDoingActionsProxy(Hero hero, HeroAction... actions) {
            super(hero);
            this.actions = actions;
        }

        @Override
        public void run() {
            closeEyes();
            for (HeroAction action : actions) {
                action.run();
            }
            openEyes();
        }

        @Override
        public boolean isProxy() {
            return true;
        }

        private void closeEyes() {
            LOGGER.info("%s closed eyes".formatted(person.name));
        }

        private void openEyes() {
            LOGGER.info("%s opened eyes".formatted(person.name));
        }
    }

    static class HugAction extends GroupHeroAction {

        HugAction(Hero... persons) {
            super(persons);
        }

        @Override
        public void run() {
            LOGGER.info("Hug process between %s".formatted(PersonUtilityHelper.getPersonNamesByPersons(persons)));
        }
    }

    static class CloseDownAction extends GroupHeroAction {

        CloseDownAction(Hero... persons) {
            super(persons);
        }

        @Override
        public void run() {
            LOGGER.info("CloseDownAction process between %s".formatted(PersonUtilityHelper.getPersonNamesByPersons(persons))); // TODO
        }
    }

    static class HoroscopeCreatingAction extends GroupHeroAction {
        private final HoroscopeResult result;
        private final Nut karatuk;
        private final Hero princess;

        HoroscopeCreatingAction(HoroscopeResult result, Nut karatuk, Hero princess, Hero... persons) {
            super(persons);
            this.result = result;
            this.karatuk = karatuk;
            this.princess = princess;
        }

        @Override
        public void run() {
            result.action = new HeroAction() {
                @Override
                public boolean isGroupAction() {
                    return false;
                }

                @Override
                public void run() {
                    Optional.ofNullable(karatuk)
                            .map(nut -> nut.core)
                            .map(peek(nutCore -> princess.doAction(new NutCoreEatingAction(princess))))
                            .ifPresent(nutCore -> new RegainedPrincessBeautyAction(princess));
                }
            };
        }

        @Override
        public String toString() {
            return PRINCESS_BEAUTY_REGAIN_CONDITION;
        }
    }

    static class HoroscopeResult {
        private HeroAction action;

        public HeroAction getAction() {
            return action;
        }

        public void setAction(HeroAction action) {
            this.action = action;
        }
    }

    static class HoroscopeResultReadingAction extends GroupHeroAction {
        private final HoroscopeResult result;

        HoroscopeResultReadingAction(HoroscopeResult result, Hero... persons) {
            super(persons);
            this.result = result;
        }

        @Override
        public void run() {
            LOGGER.info("%s read horoscope result precondition: %s"
                    .formatted(
                            PersonUtilityHelper.getPersonNamesByPersons(persons),
                            result.action
                    )
            );
        }
    }

    static class BookLearnActionUntilNight extends GroupHeroAction {
        private final BookHeap bookHeap;

        BookLearnActionUntilNight(BookHeap bookHeap, Hero... persons) {
            super(persons);
            this.bookHeap = bookHeap;
        }

        @Override
        public void run() {
//            bookHeap.
            System.out.printf("CloseDownAction process between %s%n", PersonUtilityHelper.getPersonNamesByPersons(persons)); // TODO
        }

        private void learnBook(Book book) {
            LOGGER.info("Learning book %s".formatted(book)); // todo
        }
    }

    private static class PersonUtilityHelper{
        public static List<String> getPersonNamesByPersons(List<Hero> persons) {
            return persons.stream().map(p -> p.name).toList();
        }
    }
}