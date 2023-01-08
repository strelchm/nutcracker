import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public class NutcrackerApp {
    private static final Logger LOGGER = Logger.getLogger(NutcrackerApp.class.getName());
    public static final int BOOK_HEAP_BOOK_COUNT = 20;
    public static final Pair<Double, WeightUnit> KRAKATUK_ARMOR_PIERCING_DEGREE = new Pair<>(48.0, WeightUnit.POUND);
    protected static final BookSubject[] BOOK_SUBJECTS = {BookSubject.SYMPATHIES, BookSubject.ANTIPATHIES,
            BookSubject.INSTINCT, BookSubject.ETC_WISE_THINGS};
    public static final int NUTCRACKER_STEPS_WITH_NUT_CORE_NUMBER = 7;
    public static final long TIME_UNTIL_NIGHT_MS = 5000L;
    public static final String PRINCESS_BEAUTY_REGAIN_CONDITION = "The beauty of the princess will return " +
            "to her again if the Krakatuk nut is found and the princess is given to eat its delicious kernel";
    public static final int STAR_COUNT = 100;
    public static final long LEARN_BOOK_PROCESSING_TIME = 300L;
    public static final long HOROSCOPE_LINE_PREPARING_TIME = 50L;


    public static void main(String[] args) {
        HeroFactory heroFactory = new HeroFactory();

        Hero astronomer = heroFactory.createHero(HeroType.ASTRONOMER);
        Hero drosselmaster = heroFactory.createHero(HeroType.DROSSELMASTER);
        Hero princess = heroFactory.createHero(HeroType.PRINCESS);
        Hero krakatuk = heroFactory.createHero(HeroType.KRAKATUK);

        astronomer.addFriends(new PersonFriend(drosselmaster, PersonFriendType.BOSOM));
        drosselmaster.addFriends(new PersonFriend(astronomer, PersonFriendType.BOSOM));

        // Horoscope creating part
        HeroAction askAction = new QuestioningAction(drosselmaster, new DialogAction(drosselmaster, astronomer));
        drosselmaster.doAction(askAction);

        if (astronomer.containsFriends(drosselmaster) && drosselmaster.containsFriends(astronomer)) {
            HeroAction cryingHugAction = new CryingActionProxy.CryingActionProxyBuilder()
                    .setAction(new HugAction(drosselmaster, astronomer))
                    .setPersons(drosselmaster)
                    .setPersons(astronomer)
                    .build();
            drosselmaster.doAction(cryingHugAction);
        }

        new CloseDownAction(astronomer, drosselmaster).run();

        new BookLearnUntilNightAction(getBookHeap(BOOK_HEAP_BOOK_COUNT, BOOK_SUBJECTS), astronomer, drosselmaster).run();

        List<Star> stars = getStars(STAR_COUNT);
        astronomer.doAction(new TelescopePointedAction(astronomer, stars));

        HoroscopeResult result = new HoroscopeResult();
        new HoroscopeCreatingAction(result, (Nut) krakatuk, astronomer, drosselmaster).run();
        new HoroscopeResultReadingAction(result, astronomer, drosselmaster).run();

        // Krakatuk description
        Nutcracker nutcracker = (Nutcracker) heroFactory.createHero(HeroType.NUTCRACKER);
        Optional.ofNullable(krakatuk).map(Nut.class::cast) // condition chain
                .filter(nut -> nutcracker.isNeverShaved() && !nutcracker.isWearBoots())
                .map(peek(nut -> nutcracker.doAction(new SingleHeroAction(nutcracker) {
                    @Override
                    public void run() {
                        nut.crack();
                    }
                })))
                .map(peek(nut -> {
                    GiveNutCoreAction giveNutCoreAction = new GiveNutCoreAction(nut.core, nutcracker, princess);
                    ClosedEyesDoingActionsProxy closedEyesDoingActionsProxy =
                            new ClosedEyesDoingActionsProxy.ClosedEyesDoingActionsProxyBuilder()
                                    .setHero(nutcracker)
                                    .addAction(giveNutCoreAction)
                                    .addAction(new FallBackWithoutStumbleAction(nutcracker,
                                            NUTCRACKER_STEPS_WITH_NUT_CORE_NUMBER))
                                    .build();
                    nutcracker.doAction(closedEyesDoingActionsProxy);
                }))
                .ifPresent(nut -> princess.doAction(new RegainedPrincessBeautyAction(princess)));
    }

    private static List<Star> getStars(int starCount) {
        List<Star> stars = new ArrayList<>();
        for (int i = 0; i < starCount; i++) {
            stars.add(new Star());
        }
        return stars;
    }

    private static BookHeap getBookHeap(int bookHeapBookCount, BookSubject[] bookSubjects) {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < bookHeapBookCount; i++) {
            books.add(new Book("Book %d".formatted(i)));
        }
        return new BookHeap(books, bookSubjects);
    }

    // BUSINESS DBO

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

    private static class HoroscopeLine {

    }

    private static class Horoscope {
        private final HoroscopeLine[] lines = new HoroscopeLine[50];

        public void prepare() {
            LOGGER.info("Start horoscope preparing");
            for (int i = 0; i < lines.length; i++) {
                sleep(HOROSCOPE_LINE_PREPARING_TIME);
                LOGGER.info("Horoscope line resolving");
                lines[i] = new HoroscopeLine();
            }
            LOGGER.info("Finish horoscope preparing");
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

    private enum WeightUnit {
        KG,
        POUND
    }

    public static class HeroFactory {
        public Hero createHero(HeroType heroType) {
            return switch (heroType) {
                case DROSSELMASTER,
                        ASTRONOMER,
                        PRINCESS -> new HumanHero(heroType.heroName);
                case NUTCRACKER -> new Nutcracker(true, false);
                case KRAKATUK -> new Nut(heroType.heroName, KRAKATUK_ARMOR_PIERCING_DEGREE);
                default -> throw new UnsupportedOperationException("Unknown hero type %s".formatted(heroType.name()));
            };
        }
    }

    private enum HeroType {
        ASTRONOMER("Astronomer"),
        NUTCRACKER("Nutcracker"),
        KRAKATUK("Krakatuk"),
        DROSSELMASTER("Drosselmaster"),
        PRINCESS("Princess");

        private final String heroName;

        HeroType(String heroName) {
            this.heroName = heroName;
        }
    }

    private static class Hero {
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

    private static class HumanHero extends Hero {
        private final List<PersonFriend> friends = new ArrayList<>();

        public HumanHero(String name) {
            super(name);
        }

        @Override
        public boolean isHuman() {
            return true;
        }

        @Override
        public void addFriends(PersonFriend... friends) {
            if (Arrays.stream(friends).anyMatch(friend -> this.equals(friend.person))) {
                throw new UnsupportedOperationException("Can not instantiate yourself as friend");
            }
            Collections.addAll(this.friends, friends);
        }

        @Override
        public boolean containsFriends(Hero... inputFriends) {
            List<Hero> persons = friends.stream().map(fr -> fr.person).toList();
            return Arrays.stream(inputFriends).allMatch(persons::contains);
        }
    }

    private static class Nutcracker extends Hero {
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

    private interface HeroAction extends Runnable {
        boolean isGroupAction();

        default boolean isProxy() {
            return false;
        }
    }

    private abstract static class SingleHeroAction implements HeroAction {
        protected final Hero person;

        SingleHeroAction(Hero person) {
            this.person = person;
        }

        @Override
        public boolean isGroupAction() {
            return false;
        }
    }


    private abstract static class GroupHeroAction implements HeroAction {
        protected final List<Hero> persons;

        public GroupHeroAction(Hero... persons) {
            this.persons = Arrays.stream(persons).toList();
        }

        @Override
        public boolean isGroupAction() {
            return true;
        }
    }

    // SINGLE HERO ACTION

    private static class QuestioningAction extends SingleHeroAction {
        private final HeroAction question;

        public QuestioningAction(Hero person, HeroAction askFutureAction) {
            super(person);
            this.question = askFutureAction;
        }

        @Override
        public void run() {
            LOGGER.info("%s asks for %s".formatted(person.name, question)); // not to do smth, just ask how to do it
        }
    }

    private static class FallBackWithoutStumbleAction extends SingleHeroAction {
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

    private static class TelescopePointedAction extends SingleHeroAction {
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

    private static class GiveNutCoreAction extends SingleHeroAction {
        private final Hero receiver;
        private final NutCore nutCore;

        public GiveNutCoreAction(NutCore nutCore, Hero person, Hero receiver) {
            super(person);
            this.nutCore = nutCore;
            this.receiver = receiver;
        }

        @Override
        public void run() {
            LOGGER.info("%s gives Krakatuk's nut core %s to %s".formatted(person.name, nutCore, receiver.name));
        }
    }

    private static class NutCoreEatingAction extends SingleHeroAction {
        public NutCoreEatingAction(Hero person) {
            super(person);
        }

        @Override
        public void run() {
            LOGGER.info("Princess eat tasty Krakatuk nut core");
        }
    }

    private static class RegainedPrincessBeautyAction extends SingleHeroAction {
        public RegainedPrincessBeautyAction(Hero person) {
            super(person);
        }

        @Override
        public void run() {
            LOGGER.info("The princess was regained her lost beauty");
        }
    }

    private static class DialogAction extends GroupHeroAction {

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

    // GROUP HERO ACTION

    private static class HugAction extends GroupHeroAction {

        HugAction(Hero... persons) {
            super(persons);
        }

        @Override
        public void run() {
            LOGGER.info("Hug process between %s".formatted(PersonUtilityHelper.getPersonNamesByPersons(persons)));
        }
    }

    private static class CloseDownAction extends GroupHeroAction {

        CloseDownAction(Hero... persons) {
            super(persons);
        }

        @Override
        public void run() {
            LOGGER.info("CloseDownAction process between %s"
                    .formatted(PersonUtilityHelper.getPersonNamesByPersons(persons)));
        }
    }

    private static class HoroscopeCreatingAction extends GroupHeroAction {
        private final HoroscopeResult result;
        private final Nut krakatuk;
        private final Hero princess;

        HoroscopeCreatingAction(HoroscopeResult result, Nut krakatuk, Hero princess, Hero... persons) {
            super(persons);
            this.result = result;
            this.krakatuk = krakatuk;
            this.princess = princess;
        }

        @Override
        public void run() {
            Horoscope horoscope = new Horoscope();
            horoscope.prepare();

            result.action = new HeroAction() {
                @Override
                public boolean isGroupAction() {
                    return false;
                }

                @Override
                public void run() {
                    Optional.ofNullable(krakatuk)
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

    private static class HoroscopeResult {
        private HeroAction action;

        public HeroAction getAction() {
            return action;
        }

        public void setAction(HeroAction action) {
            this.action = action;
        }
    }

    private static class HoroscopeResultReadingAction extends GroupHeroAction {
        private final HoroscopeResult result;

        HoroscopeResultReadingAction(HoroscopeResult result, Hero... persons) {
            super(persons);
            this.result = result;
        }

        @Override
        public void run() {
            LOGGER.info("%s read horoscope result precondition with delight: %s"
                    .formatted(
                            PersonUtilityHelper.getPersonNamesByPersons(persons),
                            result.action
                    )
            );
        }
    }

    private static class BookLearnUntilNightAction extends GroupHeroAction {
        private final BookHeap bookHeap;
        private final ForkJoinPool customForkJoinPool; // not to use ForkJoinPool.commonPool, but use (optimistically) thread per hero

        BookLearnUntilNightAction(BookHeap bookHeap, Hero... heroes) {
            super(heroes);
            this.bookHeap = bookHeap;
            this.customForkJoinPool = new ForkJoinPool(heroes.length); // custom work dividing between every person in parallel
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < TIME_UNTIL_NIGHT_MS) {
                customForkJoinPool.submit(() -> bookHeap.books.parallelStream().forEach(this::learnBook));
            }
            LOGGER.info("Night came");
        }

        private void learnBook(Book book) {
            sleep(LEARN_BOOK_PROCESSING_TIME);
            LOGGER.info("Learning book %s".formatted(book.bookName));
        }
    }

    // PROXY HERO ACTION

    private static class CryingActionProxy extends GroupHeroAction {
        private final HeroAction action;

        private CryingActionProxy(HeroAction action, Hero... crybabies) {
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

        public static class CryingActionProxyBuilder {
            private HeroAction action;
            private final List<Hero> persons = new ArrayList<>();

            public CryingActionProxyBuilder setAction(HeroAction action) {
                this.action = action;
                return this;
            }

            public CryingActionProxyBuilder setPersons(Hero hero) {
                persons.add(hero);
                return this;
            }

            public CryingActionProxy build() {
                return new CryingActionProxy(action, persons.toArray(new Hero[0]));
            }
        }
    }

    private static class ClosedEyesDoingActionsProxy extends SingleHeroAction {
        private final HeroAction[] actions;

        private ClosedEyesDoingActionsProxy(Hero hero, HeroAction... actions) {
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

        public static class ClosedEyesDoingActionsProxyBuilder {
            private Hero hero;
            private final List<HeroAction> actions = new ArrayList<>();

            public ClosedEyesDoingActionsProxyBuilder setHero(Hero hero) {
                this.hero = hero;
                return this;
            }

            public ClosedEyesDoingActionsProxyBuilder addAction(HeroAction action) {
                actions.add(action);
                return this;
            }

            public ClosedEyesDoingActionsProxy build() {
                return new ClosedEyesDoingActionsProxy(hero, actions.toArray(new HeroAction[0]));
            }
        }
    }

    // UTILITY CLASSES

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

    private static class PersonUtilityHelper {
        public static List<String> getPersonNamesByPersons(List<Hero> persons) {
            return persons.stream().map(p -> p.name).toList();
        }
    }

    private static <T> UnaryOperator<T> peek(Consumer<T> c) {
        return x -> {
            c.accept(x);
            return x;
        };
    }

    private static void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}