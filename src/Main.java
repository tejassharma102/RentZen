import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    static final Color NAV=new Color(15,23,42), BLUE=new Color(37,99,235),
            BG=new Color(245,247,251), TEXT=new Color(17,24,39),
            MUTED=new Color(100,116,139), GREEN=new Color(22,163,74);

    static JFrame frame;
    static JPanel content;
    static JLabel title;
    static String currentUser="Tejas", accountType="Owner & Renter";

    static final ArrayList<Device> devices=new ArrayList<>();
    static final ArrayList<Rental> rentals=new ArrayList<>();
    static final ArrayList<Device> myListings=new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { seed(); showLogin(); });
    }

    static void seed() {
        devices.add(new Device("RM-101","Sony A7 III Camera","Camera","₹1,200/day","Available","Rohan"));
        devices.add(new Device("RM-102","DJI Mini 4 Pro Drone","Drone","₹1,500/day","Available","Meera"));
        devices.add(new Device("RM-103","MacBook Pro 14","Laptop","₹1,000/day","Rented","Arjun"));
        devices.add(new Device("RM-104","JBL Party Speaker","Audio","₹700/day","Available","Kabir"));
        devices.add(new Device("RM-105","PlayStation 5","Gaming","₹800/day","Available","Neha"));
        devices.add(new Device("RM-106","GoPro Hero 12","Camera","₹650/day","Available","Vikram"));

        myListings.add(new Device("MY-201","Canon EOS R6","Camera","₹1,100/day","Available","Alex"));
        myListings.add(new Device("MY-202","Gaming Laptop","Laptop","₹900/day","Available","Alex"));

        rentals.add(new Rental("RENT-001","Sony A7 III Camera","Alex","Rohan",
                "10 Sep → 12 Sep","Confirmed","₹2,400"));
        rentals.add(new Rental("RENT-002","MacBook Pro 14","Alex","Arjun",
                "20 Sep → 22 Sep","Pending","₹2,000"));
    }

    static void showLogin() {
        frame=new JFrame("RentZen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100,700);
        frame.setLocationRelativeTo(null);

        JPanel bg=new JPanel(new GridBagLayout());
        bg.setBackground(NAV);

        JPanel card=new JPanel(new BorderLayout(0,18));
        card.setPreferredSize(new Dimension(470,510));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(42,48,42,48));

        JPanel head=new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head,BoxLayout.Y_AXIS));

        JLabel logo=label("RentZen",32,BLUE);
        logo.setFont(new Font("Segoe UI",Font.BOLD,32));
        head.add(logo);
        head.add(Box.createVerticalStrut(5));
        head.add(label("Rent devices. Earn from your gear.",14,MUTED));

        JPanel form=new JPanel(new GridLayout(0,1,0,9));
        form.setOpaque(false);

        form.add(label("Your name",13,TEXT));
        JTextField name=new JTextField("Tejas");
        styleField(name);
        form.add(name);

        form.add(label("Account type",13,TEXT));
        JComboBox<String> type=new JComboBox<>(
                new String[]{"Owner & Renter","Renter only","Owner only"});
        type.setToolTipText("Choose what you want to do on RentMate.");
        form.add(type);

        form.add(label("Email",13,TEXT));
        JTextField email=new JTextField("tejas@example.com");
        styleField(email);
        form.add(email);

        JButton login=button("Continue to RentZen  →",BLUE);
        login.addActionListener(e -> {
            currentUser=name.getText().isBlank() ? "Tejas" : name.getText().trim();
            accountType=(String)type.getSelectedItem();
            showApp();
        });

        JLabel note=label("Demo mode • local prototype • no real payments",11,MUTED);
        JPanel bottom=new JPanel(new BorderLayout(0,8));
        bottom.setOpaque(false);
        bottom.add(login);
        bottom.add(note,BorderLayout.SOUTH);

        card.add(head,BorderLayout.NORTH);
        card.add(form,BorderLayout.CENTER);
        card.add(bottom,BorderLayout.SOUTH);
        bg.add(card);

        frame.setContentPane(bg);
        frame.setVisible(true);
    }

    static void showApp() {
        JPanel root=new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(sidebar(),BorderLayout.WEST);

        JPanel main=new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(topbar(),BorderLayout.NORTH);

        content=new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0,28,28,28));

        main.add(content,BorderLayout.CENTER);
        root.add(main,BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setSize(1280,800);
        frame.setLocationRelativeTo(null);
        dashboard();
        frame.revalidate();
        frame.repaint();
    }

    static JPanel sidebar() {
        JPanel side=new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(245,0));
        side.setBackground(NAV);
        side.setBorder(new EmptyBorder(25,16,22,16));

        JPanel top=new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));

        JLabel logo=label("RentZen",28,Color.WHITE);
        logo.setFont(new Font("Segoe UI",Font.BOLD,28));
        top.add(logo);
        top.add(Box.createVerticalStrut(4));
        top.add(label("COMMUNITY RENTAL MARKETPLACE",9,
                new Color(148,163,184)));
        top.add(Box.createVerticalStrut(25));

        JPanel nav=new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav,BoxLayout.Y_AXIS));

        addNav(nav,"⌂","Home",Main::dashboard);
        addNav(nav,"⌕","Find Devices",Main::browse);

        if(!accountType.equals("Renter only"))
            addNav(nav,"＋","My Listings",Main::listings);

        if(!accountType.equals("Owner only"))
            addNav(nav,"◷","My Rentals",Main::rentals);

        if(!accountType.equals("Renter only"))
            addNav(nav,"₹","Earnings",Main::earnings);

        addNav(nav,"♡","Saved Devices",Main::saved);

        JPanel profile=new JPanel(new BorderLayout(9,0));
        profile.setOpaque(false);
        profile.add(label("●",18,new Color(74,222,128)),BorderLayout.WEST);

        JPanel info=new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));
        info.add(label(currentUser,13,Color.WHITE));
        info.add(label(accountType,10,new Color(148,163,184)));
        profile.add(info);

        side.add(top,BorderLayout.NORTH);
        side.add(nav,BorderLayout.CENTER);
        side.add(profile,BorderLayout.SOUTH);
        return side;
    }

    static void addNav(JPanel panel,String icon,String text,Runnable action) {
        JButton b=new JButton(icon+"   "+text);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(215,45));
        b.setPreferredSize(new Dimension(215,45));
        b.setForeground(new Color(203,213,225));
        b.setBackground(NAV);
        b.setBorder(new EmptyBorder(0,14,0,5));
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI",Font.PLAIN,14));
        b.addActionListener(e -> action.run());
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(30,41,59));
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(NAV);
            }
        });
        panel.add(b);
        panel.add(Box.createVerticalStrut(4));
    }

    static JPanel topbar() {
        JPanel bar=new JPanel(new BorderLayout(12,0));
        bar.setBackground(Color.WHITE);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,new Color(226,232,240)),
                new EmptyBorder(18,28,18,28)));

        title=label("Home",24,TEXT);
        title.setFont(new Font("Segoe UI",Font.BOLD,24));

        JTextField search=new JTextField();
        search.setPreferredSize(new Dimension(260,40));
        styleField(search);
        search.setToolTipText("Search devices");

        JButton add=button("+  List My Device",BLUE);
        add.addActionListener(e -> addListing());

        JButton logout=button("Logout",new Color(226,232,240));
        logout.setForeground(TEXT);
        logout.addActionListener(e -> showLogin());

        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        right.setOpaque(false);
        right.add(search);
        if(!accountType.equals("Renter only")) right.add(add);
        right.add(logout);

        bar.add(title,BorderLayout.WEST);
        bar.add(right,BorderLayout.EAST);
        return bar;
    }

    static void dashboard() {
        page("Home");
        JPanel all=vertical();

        JLabel welcome=label("Hello, "+currentUser+" 👋",23,TEXT);
        welcome.setFont(new Font("Segoe UI",Font.BOLD,23));
        all.add(welcome);
        all.add(label("A simple marketplace for borrowing and earning from everyday devices.",
                13,MUTED));
        all.add(Box.createVerticalStrut(18));

        JPanel stats=new JPanel(new GridLayout(1,4,14,0));
        stats.setOpaque(false);
        stats.add(stat("Devices Listed","126","Across the marketplace"));
        stats.add(stat("Active Rentals","02","Your current rentals"));

        if(!accountType.equals("Renter only")) {
            stats.add(stat("My Listings",String.valueOf(myListings.size()),"Devices you own"));
            stats.add(stat("This Month","₹4,850","Estimated earnings"));
        } else {
            stats.add(stat("Saved Devices","06","Ready to compare"));
            stats.add(stat("Spent This Month","₹2,400","Rental total"));
        }

        all.add(stats);
        all.add(Box.createVerticalStrut(20));

        JPanel lower=new JPanel(new GridLayout(1,2,14,0));
        lower.setOpaque(false);
        lower.add(featuredPanel());
        lower.add(quickActions());
        all.add(lower);

        content.add(all);
    }

    static JPanel featuredPanel() {
        JPanel panel=card();
        panel.setLayout(new BorderLayout(0,12));

        JLabel heading=label("Popular devices",17,TEXT);
        heading.setFont(new Font("Segoe UI",Font.BOLD,17));
        panel.add(heading,BorderLayout.NORTH);

        JPanel list=new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));

        for(int i=0;i<Math.min(4,devices.size());i++) {
            Device d=devices.get(i);
            JPanel row=new JPanel(new BorderLayout(10,0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(8,0,8,0));

            JPanel info=new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));
            info.add(label(d.name,13,TEXT));
            info.add(label(d.category+"  •  Owner: "+d.owner,11,MUTED));

            JLabel price=label(d.price,13,BLUE);
            price.setFont(new Font("Segoe UI",Font.BOLD,13));

            row.add(info);
            row.add(price,BorderLayout.EAST);
            list.add(row);
        }

        panel.add(list);
        return panel;
    }

    static JPanel quickActions() {
        JPanel panel = card();
        panel.setLayout(new BorderLayout(0, 12));

        JLabel heading = label("Quick Actions", 17, TEXT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 17));
        panel.add(heading, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(3, 1, 0, 10));
        actions.setOpaque(false);

        JButton browse = button(
                accountType.equals("Owner only")
                        ? "⌕  Browse marketplace"
                        : "⌕  Find a device to rent",
                new Color(239,246,255)
        );
        browse.setForeground(BLUE);
        browse.addActionListener(e -> browse());
        actions.add(browse);

        if (!accountType.equals("Renter only")) {
            JButton list = button("＋  List my device", new Color(239,246,255));
            list.setForeground(BLUE);
            list.addActionListener(e -> addListing());
            actions.add(list);
        } else {
            JButton rentalsButton = button("◷  Check my rentals", new Color(239,246,255));
            rentalsButton.setForeground(BLUE);
            rentalsButton.addActionListener(e -> rentals());
            actions.add(rentalsButton);
        }

        if (accountType.equals("Owner only")) {
            JButton earningsButton = button("₹  View earnings", new Color(239,246,255));
            earningsButton.setForeground(BLUE);
            earningsButton.addActionListener(e -> earnings());
            actions.add(earningsButton);
        } else {
            JButton rentalsButton = button("◷  Check my rentals", new Color(239,246,255));
            rentalsButton.setForeground(BLUE);
            rentalsButton.addActionListener(e -> rentals());
            actions.add(rentalsButton);
        }

        panel.add(actions);
        return panel;
    }

    static void browse() {
        page("Find Devices");

        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JLabel h = label("Devices available for rent", 18, TEXT);
        h.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JTextField search = new JTextField();
        search.setPreferredSize(new Dimension(230, 38));
        styleField(search);

        JTable table = deviceTable();

        JButton action = button(
                accountType.equals("Owner only") ? "View Device" : "Rent Selected",
                BLUE
        );

        action.addActionListener(e -> {
            if (accountType.equals("Owner only")) {
                showOwnerDeviceInfo(table);
            } else {
                rentSelected();
            }
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        controls.add(search);
        controls.add(action);

        heading.add(h, BorderLayout.WEST);
        heading.add(controls, BorderLayout.EAST);

        search.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String query = search.getText().trim();
                TableRowSorter<TableModel> sorter =
                        new TableRowSorter<>(table.getModel());
                table.setRowSorter(sorter);

                sorter.setRowFilter(query.isEmpty()
                        ? null
                        : RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
            }
        });

        panel.add(heading, BorderLayout.NORTH);
        panel.add(scroll(table), BorderLayout.CENTER);
        content.add(panel);
    }

    static void showOwnerDeviceInfo(JTable table) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Select a device to view its details."
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String id = table.getModel().getValueAt(modelRow, 0).toString();

        for (Device device : devices) {
            if (device.id.equals(id)) {
                showSuccess(
                        "Device Details",
                        "Device: " + device.name + "\n" +
                        "Category: " + device.category + "\n" +
                        "Price: " + device.price + "\n" +
                        "Status: " + device.status + "\n" +
                        "Owner: " + device.owner
                );
                return;
            }
        }
    }

    static void listings() {
        page("My Listings");

        JPanel panel=new JPanel(new BorderLayout(0,15));
        panel.setOpaque(false);

        JPanel head=new JPanel(new BorderLayout());
        head.setOpaque(false);

        JLabel h=label("Devices I have listed",18,TEXT);
        h.setFont(new Font("Segoe UI",Font.BOLD,18));

        JButton add=button("+  List New Device",BLUE);
        add.addActionListener(e -> addListing());

        head.add(h,BorderLayout.WEST);
        head.add(add,BorderLayout.EAST);

        Object[][] data=new Object[myListings.size()][5];

        for(int i=0;i<myListings.size();i++) {
            Device d=myListings.get(i);
            data[i]=new Object[]{d.id,d.name,d.category,d.status,d.price};
        }

        panel.add(head,BorderLayout.NORTH);
        panel.add(scroll(table(data,new String[]{"ID","Device","Category","Status","Price"})));
        content.add(panel);
    }

    static void rentals() {
        page("My Rentals");

        JPanel all = vertical();

        JLabel h = label("Your rental activity", 18, TEXT);
        h.setFont(new Font("Segoe UI", Font.BOLD, 18));

        all.add(h);
        all.add(Box.createVerticalStrut(12));

        ArrayList<Rental> mine = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.renter.equals(currentUser)) {
                mine.add(rental);
            }
        }

        JPanel tablePanel = panelTable("Current & recent rentals", rentalTable(mine));
        all.add(tablePanel);
        all.add(Box.createVerticalStrut(10));

        JButton returnButton = button("↩  Mark Selected Rental Returned", BLUE);
        returnButton.addActionListener(e -> markRentalReturned());
        all.add(returnButton);

        content.add(all);
    }

    static void markRentalReturned() {
        JTable table = findFirstTable(content);

        if (table == null || table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Select a rental first."
            );
            return;
        }

        int row = table.convertRowIndexToModel(table.getSelectedRow());
        String rentalId = table.getModel().getValueAt(row, 0).toString();

        for (Rental rental : rentals) {
            if (rental.id.equals(rentalId) && rental.renter.equals(currentUser)) {
                if (rental.status.equals("Returned")) {
                    JOptionPane.showMessageDialog(frame, "This rental is already marked returned.");
                    return;
                }

                rental.status = "Returned";

                for (Device device : devices) {
                    if (device.name.equals(rental.device)
                            && device.owner.equals(rental.owner)) {
                        device.status = "Available";
                    }
                }

                showSuccess(
                        "Return Completed",
                        "Return recorded successfully for " + rental.device + "."
                );
                rentals();
                return;
            }
        }

        JOptionPane.showMessageDialog(frame, "Rental not found.");
    }

    static void earnings() {
        page("Earnings");
        JPanel all=vertical();

        JPanel stats=new JPanel(new GridLayout(1,3,14,0));
        stats.setOpaque(false);
        stats.add(stat("This Month","₹4,850","Estimated"));
        stats.add(stat("Total Earned","₹18,620","All time"));
        stats.add(stat("Completed Rentals","31","Successful rentals"));

        all.add(stats);
        all.add(Box.createVerticalStrut(18));

        JPanel note=card();
        note.setLayout(new BorderLayout(0,8));

        JLabel h=label("Owner earnings",17,TEXT);
        h.setFont(new Font("Segoe UI",Font.BOLD,17));
        note.add(h,BorderLayout.NORTH);
        note.add(label("Your earnings dashboard gives a quick view of rental income from the devices you have listed. Real payment processing would be connected in a production version.",13,MUTED));

        all.add(note);
        content.add(all);
    }

    static void saved() {
        page("Saved Devices");
        JPanel all=vertical();

        JLabel h=label("Devices you may want later",18,TEXT);
        h.setFont(new Font("Segoe UI",Font.BOLD,18));
        all.add(h);
        all.add(Box.createVerticalStrut(12));

        for(Device d:devices) {
            JPanel row=card();
            row.setLayout(new BorderLayout(10,0));
            row.add(label("♡  "+d.name,14,TEXT));
            row.add(label(d.price+"   •   "+d.category,13,BLUE),BorderLayout.EAST);
            all.add(row);
            all.add(Box.createVerticalStrut(8));
        }

        content.add(all);
    }

    static void addListing() {
        if(accountType.equals("Renter only")) {
            JOptionPane.showMessageDialog(frame,"Your account is set to renter-only.");
            return;
        }

        JTextField name=new JTextField();
        JTextField price=new JTextField("₹500/day");
        JComboBox<String> category=new JComboBox<>(new String[]{
                "Camera","Laptop","Drone","Audio","Gaming","Projector","Other"
        });

        JPanel form=form();
        form.add(label("Device name",13,TEXT)); form.add(name);
        form.add(label("Category",13,TEXT)); form.add(category);
        form.add(label("Rental price",13,TEXT)); form.add(price);

        int result=JOptionPane.showConfirmDialog(
                frame,form,"List Your Device",
                JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);

        if(result==JOptionPane.OK_OPTION && !name.getText().isBlank()) {
            Device listing = new Device(
                    "MY-" + String.format("%03d", myListings.size() + 203),
                    name.getText().trim(),
                    (String) category.getSelectedItem(),
                    price.getText().trim(),
                    "Available",
                    currentUser
            );

            myListings.add(listing);
            devices.add(listing);

            showSuccess(
                    "Device Listed",
                    "Your device is now live on RentMate and can be discovered by renters."
            );
            listings();
        }
    }

    static void rentSelected() {
        if (accountType.equals("Owner only")) {
            JOptionPane.showMessageDialog(
                    frame,
                    "This account is owner-only. Switch to Owner & Renter to rent devices."
            );
            return;
        }

        // The booking button should act on the row the user selected.
        // It is intentionally kept simple for the college-project version.
        // The real application would send this request to a backend.
        Container parent = content;
        JTable table = findFirstTable(parent);

        if (table == null || table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please select a device from the list first."
            );
            return;
        }

        int row = table.convertRowIndexToModel(table.getSelectedRow());
        String deviceId = table.getModel().getValueAt(row, 0).toString();

        Device selected = null;
        for (Device device : devices) {
            if (device.id.equals(deviceId)) {
                selected = device;
                break;
            }
        }

        if (selected == null) {
            JOptionPane.showMessageDialog(frame, "The selected device could not be found.");
            return;
        }

        if (!selected.status.equals("Available")) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Sorry, " + selected.name + " is currently " + selected.status.toLowerCase() + "."
            );
            return;
        }

        JPanel form = form();
        JTextField dates = new JTextField("12 Sep → 14 Sep");
        form.add(label("Device", 13, TEXT));
        form.add(label(selected.name, 13, TEXT));
        form.add(label("Owner", 13, TEXT));
        form.add(label(selected.owner, 13, TEXT));
        form.add(label("Rental price", 13, TEXT));
        form.add(label(selected.price, 13, BLUE));
        form.add(label("Rental dates", 13, TEXT));
        form.add(dates);

        int result = JOptionPane.showConfirmDialog(
                frame,
                form,
                "Confirm Rental",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        rentals.add(new Rental(
                "RENT-" + String.format("%03d", rentals.size() + 1),
                selected.name,
                currentUser,
                selected.owner,
                dates.getText().trim(),
                "Confirmed",
                selected.price
        ));

        selected.status = "Rented";

        showSuccess(
                "Rental Confirmed",
                "Your rental has been confirmed!\n\n" +
                "Device: " + selected.name + "\n" +
                "Owner: " + selected.owner + "\n" +
                "Dates: " + dates.getText().trim() + "\n" +
                "Rental ID: " + rentals.get(rentals.size() - 1).id
        );

        rentals();
    }

    static JTable findFirstTable(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTable) {
                return (JTable) component;
            }
            if (component instanceof Container) {
                JTable result = findFirstTable((Container) component);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    static void showSuccess(String heading, String message) {
        JOptionPane.showMessageDialog(
                frame,
                message,
                heading,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    static JTable deviceTable() {
        Object[][] data=new Object[devices.size()][6];

        for(int i=0;i<devices.size();i++) {
            Device d=devices.get(i);
            data[i]=new Object[]{d.id,d.name,d.category,d.price,d.status,d.owner};
        }

        return table(data,new String[]{
                "ID","Device","Category","Price","Status","Owner"
        });
    }

    static JTable rentalTable() {
        return rentalTable(rentals);
    }

    static JTable rentalTable(ArrayList<Rental> list) {
        Object[][] data = new Object[list.size()][7];

        for (int i = 0; i < list.size(); i++) {
            Rental rental = list.get(i);
            data[i] = new Object[]{
                    rental.id,
                    rental.device,
                    rental.renter,
                    rental.owner,
                    rental.dates,
                    rental.status,
                    rental.amount
            };
        }

        return table(
                data,
                new String[]{"Rental ID", "Device", "Renter", "Owner",
                        "Dates", "Status", "Amount"}
        );
    }

    static JTable table(Object[][] data,String[] columns) {
        JTable table=new JTable(data,columns);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.setGridColor(new Color(241,245,249));
        table.setSelectionBackground(new Color(219,234,254));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader header=table.getTableHeader();
        header.setPreferredSize(new Dimension(0,42));
        header.setFont(new Font("Segoe UI",Font.BOLD,12));
        header.setBackground(new Color(248,250,252));
        header.setForeground(MUTED);

        return table;
    }

    static JPanel stat(String heading,String value,String note) {
        JPanel panel=card();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        panel.add(label(heading,12,MUTED));

        JLabel valueLabel=label(value,29,TEXT);
        valueLabel.setFont(new Font("Segoe UI",Font.BOLD,29));

        panel.add(Box.createVerticalStrut(7));
        panel.add(valueLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(label(note,11,GREEN));

        return panel;
    }

    static JPanel panelTable(String heading,JTable table) {
        JPanel panel=card();
        panel.setLayout(new BorderLayout(0,10));

        JLabel h=label(heading,17,TEXT);
        h.setFont(new Font("Segoe UI",Font.BOLD,17));

        panel.add(h,BorderLayout.NORTH);
        panel.add(scroll(table),BorderLayout.CENTER);
        return panel;
    }

    static JPanel vertical() {
        JPanel panel=new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        return panel;
    }

    static JPanel form() {
        JPanel panel=new JPanel(new GridLayout(0,2,12,12));
        panel.setBorder(new EmptyBorder(12,12,12,12));
        return panel;
    }

    static JScrollPane scroll(JTable table) {
        JScrollPane pane=new JScrollPane(table);
        pane.setBorder(new LineBorder(new Color(226,232,240)));
        pane.getViewport().setBackground(Color.WHITE);
        return pane;
    }

    static JPanel card() {
        JPanel panel=new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(226,232,240)),
                new EmptyBorder(18,18,18,18)));
        return panel;
    }

    static JButton button(String text,Color background) {
        JButton b=new JButton(text);
        b.setBackground(background);
        b.setForeground(background.equals(BLUE)?Color.WHITE:TEXT);
        b.setFont(new Font("Segoe UI",Font.BOLD,13));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10,15,10,15));
        return b;
    }

    static JLabel label(String text,int size,Color color) {
        JLabel l=new JLabel(text);
        l.setFont(new Font("Segoe UI",Font.PLAIN,size));
        l.setForeground(color);
        return l;
    }

    static void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI",Font.PLAIN,14));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(203,213,225)),
                new EmptyBorder(9,11,9,11)));
        field.setBackground(Color.WHITE);
    }

    static void page(String name) {
        title.setText(name);
        content.removeAll();
        content.revalidate();
        content.repaint();
    }

    static class Device {
        String id,name,category,price,status,owner;

        Device(String id,String name,String category,String price,
               String status,String owner) {
            this.id=id;
            this.name=name;
            this.category=category;
            this.price=price;
            this.status=status;
            this.owner=owner;
        }
    }

    static class Rental {
        String id,device,renter,owner,dates,status,amount;

        Rental(String id,String device,String renter,String owner,
               String dates,String status,String amount) {
            this.id=id;
            this.device=device;
            this.renter=renter;
            this.owner=owner;
            this.dates=dates;
            this.status=status;
            this.amount=amount;
        }
    }
}
