package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

    public BeanGetter<ClientTestDao> clientTestDao1;

    public BeanGetter<ClientRegister> clientRegister;


    private Client addClient(Charm charm) {

        Client client = new Client();
        client.id = RND.plusInt(10000);
        client.firstname = RND.str(10);
        client.lastname = RND.str(10);
        client.patronymic = RND.str(10);
        client.gender = GenderType.MALE;
        client.birthDate = new Date();
        client.charm = charm.id;
        return client;
    }

    private Charm addCharm() {

        Charm charm = new Charm();
        charm.id = RND.plusInt(1000);
        charm.name = RND.str(10);
        charm.description = RND.str(10);
        charm.energy = RND.plusInt(100);
        charm.actually = rndBoolean();
        return charm;
    }

    private ClientAddr addClientAddr(Client client) {

        ClientAddr clientAddr = new ClientAddr();
        clientAddr.client = client.id;
        clientAddr.type = AddrType.REG;
        clientAddr.street = RND.str(9);
        clientAddr.house = RND.str(9);
        clientAddr.flat = RND.str(9);
        return clientAddr;
    }

    private ClientPhone addClientPhone(Client client) {

        ClientPhone clientPhone = new ClientPhone();
        clientPhone.client = client.id;
        clientPhone.type = PhoneType.MOBILE;
        clientPhone.number = RND.plusInt(110000) + "";
        return clientPhone;
    }

    private ClientAccount addClientAccount(Client client) {

        ClientAccount clientAccount = new ClientAccount();
        clientAccount.id = RND.plusInt(10000);
        clientAccount.client = client.id;
        clientAccount.money = RND.plusInt(456);
        clientAccount.number = RND.str(10);
        clientAccount.registeredAt = new Timestamp(RND.plusInt(532));
        return clientAccount;

    }

    private TransactionType addTransactionType() {

        TransactionType transactionType = new TransactionType();

        transactionType.id = RND.plusInt(546);
        transactionType.code = RND.str(10);
        transactionType.name = RND.str(10);
        return transactionType;
    }

    private ClientAccountTransaction addClientAccountTransaction(ClientAccount clientAccount, TransactionType transactionType) {

        ClientAccountTransaction clientAccountTransaction = new ClientAccountTransaction();
        clientAccountTransaction.id = RND.plusInt(546);
        clientAccountTransaction.account = clientAccount.id;
        clientAccountTransaction.money = RND.plusInt(1000);
        clientAccountTransaction.finishedAt = new Timestamp(RND.plusInt(532));
        clientAccountTransaction.type = transactionType.id;
        return clientAccountTransaction;
    }

    private List<Integer> addDatasDao(int sizeForCycle) {

        Charm charm;
        Client client;
        ClientAccount clientAccount;
        List<Integer> id = new ArrayList<>();
        for (int i = 0; i < sizeForCycle; i++) {
            charm = addCharm();
            client = addClient(charm);
            id.add(client.id);
            clientAccount = addClientAccount(client);
            clientTestDao1.get().insertCharm(charm);
            clientTestDao1.get().insertClient(client);
            clientTestDao1.get().insertClientAccount(clientAccount);
            clientAccount.money = clientAccount.money + 50;
            clientAccount.id = clientAccount.id + 50;
            clientTestDao1.get().insertClientAccount(clientAccount);
        }
        return id;

    }

    @BeforeMethod
    public void setUp() {

        clientTestDao1.get().deleteAllClientAccountTransaction();
        clientTestDao1.get().deleteAllClientAccount();
        clientTestDao1.get().deleteAllClientAddr();
        clientTestDao1.get().deleteAllClientPhone();
        clientTestDao1.get().deleteAllClient();
        clientTestDao1.get().deleteAllTransactionType();
        clientTestDao1.get().deleteAllCharm();

    }

    private boolean rndBoolean() {

        int IntRnd = (int) (Math.random() * 10 - 5);
        if (IntRnd < 0) {
            return false;
        } else
            return true;
    }

    // TODO: asset 9/21/18 Protestiru vse list ne tolko poslednuyu i order obyazatelen a to test ne stabilnyi
    @Test
    public void testGetCharm() {

        Charm charm = new Charm();

        for (int i = 0; i < 5; i++) {
            charm = addCharm();
            clientTestDao1.get().insertCharm(charm);
        }
        //
        //
        // TODO: asset 9/21/18 vse Charm actualny bolu kerek
        List<Charm> listCharm = clientRegister.get().getCharm();
        //
        //
        System.err.println(listCharm);
        assertThat(listCharm).hasSize(5);
        assertThat(listCharm.get(4).id).isEqualTo(charm.id);
        assertThat(listCharm.get(4).name).isEqualTo(charm.name);
        assertThat(listCharm.get(4).description).isEqualTo(charm.description);
        assertThat(listCharm.get(4).energy).isEqualTo(charm.energy);
        assertThat(listCharm.get(4).actually).isEqualTo(charm.actually);

    }

    // TODO: asset 9/21/18 Ne polny test
    @Test
    public void testGetCharmById() {

        Charm ch = new Charm();
        for (int i = 0; i < 5; i++) {
            ch = addCharm();
            clientTestDao1.get().insertCharm(ch);
        }
        Integer id = 1000;
        Integer id1 = 0;
        Integer id2 = null;

//
//
        Charm charm = clientRegister.get().getCharmById(id);
        Charm charm1 = clientRegister.get().getCharmById(id1);
        Charm charm2 = clientRegister.get().getCharmById(id2);
        Charm charm3 = clientRegister.get().getCharmById(ch.id);
//
//

        assertThat(charm.id).isEqualTo(1000);
        assertThat(charm1.id).isEqualTo(0);
        assertThat(charm2).isNull();
        assertThat(clientTestDao1.get().selectCharmById(ch.id).id).isEqualTo(charm3.id);
    }


    // TODO: asset 9/21/18 Mozhno tolko sozdat Client-ta, po moemu mneniu drugie znacheni ne nuzhny
    @Test
    public void testDeleteClient() {

        Charm charm = new Charm();
        Client client = new Client();
        for (int i = 0; i < 5; i++) {
            charm = addCharm();
            client = addClient(charm);
            clientTestDao1.get().insertCharm(charm);
            clientTestDao1.get().insertClient(client);
        }

//
//
        clientRegister.get().deleteClient(client.id);

//
//
        Integer clientOfDeleted = clientTestDao1.get().getClientId(client.id);
        assertThat(clientOfDeleted).isNull();

    }


    // TODO: asset 9/21/18 Ne polnyi test drugye attributy tozhe dolzhny zapolnatsya
    // TODO: asset 9/21/18
    @Test
    public void testGetClientDetails() {

        Charm charm = new Charm();
        Client client = new Client();
        ClientAddr clientAddr = new ClientAddr();
        ClientPhone clientPhone = new ClientPhone();
        charm = addCharm();
        client = addClient(charm);
        clientPhone = addClientPhone(client);
        clientAddr = addClientAddr(client);
        clientTestDao1.get().insertCharm(charm);
        clientTestDao1.get().insertClient(client);
        clientTestDao1.get().insertClientPhone(clientPhone);
        clientTestDao1.get().insertClientAddr(clientAddr);
        clientAddr.type = AddrType.FACT;
        clientTestDao1.get().insertClientAddr(clientAddr);

//
//
        ClientDetails clientDetails = clientRegister.get().getClientDetails(client.id);
//
//
        assertThat(client.id).isEqualTo(clientDetails.id);

    }

    // TODO: asset 9/21/18 Ne obyazatelno zapolnyat vse dannye, luche protestirovat vse attributy tipa vse korrectno sokhranilas ili net
    @Test
    public void testSaveClientEdit() {

        Charm charm;
        Client client;
        ClientAddr clientAddr;
        ClientPhone clientPhone;
        ClientAccountTransaction clientAccountTransaction;
        TransactionType transactionType;
        ClientAccount clientAccount;
        charm = addCharm();
        client = addClient(charm);
        clientPhone = addClientPhone(client);
        clientAddr = addClientAddr(client);
        transactionType = addTransactionType();
        clientAccount = addClientAccount(client);
        clientAccountTransaction = addClientAccountTransaction(clientAccount, transactionType);

        clientTestDao1.get().insertCharm(charm);
        clientTestDao1.get().insertClient(client);
        clientTestDao1.get().insertClientPhone(clientPhone);
        clientTestDao1.get().insertClientAddr(clientAddr);
        clientAddr.type = AddrType.FACT;
        clientTestDao1.get().insertClientAddr(clientAddr);
        clientTestDao1.get().insertTransaction_type(transactionType);
        clientTestDao1.get().insertClientAccount(clientAccount);
        clientTestDao1.get().insertClientAccountTransaction(clientAccountTransaction);

        ClientToSave clientToSave = new ClientToSave();
        clientToSave.id = client.id;
        clientToSave.firstname = client.firstname;
        clientToSave.lastname = client.lastname;
        clientToSave.patronymic = client.patronymic;
        clientToSave.gender = client.gender;
        clientToSave.dateOfBirth = client.birthDate;
        clientToSave.characterId = charm.id;
        clientToSave.addressOfRegistration.client = clientAddr.client;
        clientToSave.addressOfRegistration.street = clientAddr.street;
        clientToSave.addressOfRegistration.house = clientAddr.house;
        clientToSave.addressOfRegistration.flat = clientAddr.flat;
        clientToSave.addressOfRegistration.type = AddrType.REG;
        clientToSave.addressOfResidence.client = clientAddr.client;
        clientToSave.addressOfResidence.street = clientAddr.street;
        clientToSave.addressOfResidence.house = clientAddr.house;
        clientToSave.addressOfResidence.flat = clientAddr.flat;
        clientToSave.addressOfResidence.type = AddrType.FACT;
        clientToSave.phone.add(clientPhone);

        //
        //
        ClientRecord clientRecord = clientRegister.get().saveClient(clientToSave);
        System.err.println(clientRecord);
        //
        //


			assertThat(clientRecord).isNotNull();
			assertThat(clientRecord.firstname).isEqualTo(clientToSave.firstname);
			assertThat(clientRecord.id).isEqualTo(clientToSave.id);

    }

    @Test
    public void testSaveClientSave() {

        Charm charm;
        Client client;
        ClientAddr clientAddr;
        ClientPhone clientPhone;
        charm = addCharm();
        client = addClient(charm);
        clientPhone = addClientPhone(client);
        clientAddr = addClientAddr(client);

        clientTestDao1.get().insertCharm(charm);
        clientTestDao1.get().insertClient(client);
        ClientToSave clientToSave = new ClientToSave();
        clientToSave.id = null;
        clientToSave.firstname = client.firstname;
        clientToSave.lastname = client.lastname;
        clientToSave.patronymic = client.patronymic;
        clientToSave.gender = client.gender;
        clientToSave.dateOfBirth = client.birthDate;
        clientToSave.characterId = charm.id;
        clientToSave.addressOfRegistration.client = null;
        clientToSave.addressOfRegistration.street = clientAddr.street;
        clientToSave.addressOfRegistration.house = clientAddr.house;
        clientToSave.addressOfRegistration.flat = clientAddr.flat;
        clientToSave.addressOfRegistration.type = AddrType.REG;
        clientToSave.addressOfResidence.client = null;
        clientToSave.addressOfResidence.street = clientAddr.street;
        clientToSave.addressOfResidence.house = clientAddr.house;
        clientToSave.addressOfResidence.flat = clientAddr.flat;
        clientToSave.addressOfResidence.type = AddrType.FACT;
        clientPhone.client=null;
        clientToSave.phone.add(clientPhone);
        Integer id = clientToSave.id;
        System.err.println("CLIENTTOSAVE:"+clientToSave);

        //
        //
        ClientRecord clientRecord = clientRegister.get().saveClient(clientToSave);
        //
        //


        assertThat(clientRecord.firstname).isEqualTo(clientToSave.firstname);
        assertThat(clientRecord.id).isNotEqualTo(id);
        assertThat(clientTestDao1.get().getClientById(clientRecord.id).id).isEqualTo(clientRecord.id);

    }

    // TODO: asset 9/21/18 Drugye attributy tozhe dolzhno proveryatsya record-a
    @Test
    public void testGetClientList() {

        Charm charm;
        Client client;
        ClientAccount clientAccount;
        List<Integer> id;
        id = addDatasDao(10);
        Collections.sort(id);

        ClientFilter clientFilter = new ClientFilter();
        clientFilter.firstname = "";
        clientFilter.lastname = "";
        clientFilter.patronymic = "";
        clientFilter.orderBy = "id";
        clientFilter.recordSize = 20;
        clientFilter.page = 0;
        clientFilter.recordTotal = 100;
        clientFilter.sort = true;
//
//
        ClientRecord clientRecord = new ClientRecord();
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(clientFilter);
        System.err.println(clientRecords);
        System.err.println(clientRecords.size());
//
//


        System.err.println(id);
        assertThat(clientRecords.size()).isEqualTo(10);
        for (int i = 0; i < 10; i++) {
            assertThat(clientRecords.get(i).id).isEqualTo(id.get(i));
        }

    }

    // TODO: asset 9/21/18 Mozhesh dlya kazhdogy clienta generit neskolko account-ov
    @Test
    public void testGetClientListByFilter() {

        Charm charm;
        Client client = new Client();
        ClientAccount clientAccount;
        List<Integer> id = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            charm = addCharm();
            client = addClient(charm);
            if (i >= 8) {
                id.add(client.id);
                client.firstname = "Nazar";
            }
            clientAccount = addClientAccount(client);
            clientTestDao1.get().insertCharm(charm);
            clientTestDao1.get().insertClient(client);
            clientTestDao1.get().insertClientAccount(clientAccount);
            clientAccount.money = clientAccount.money + 50;
            clientAccount.id = clientAccount.id + 50;
            clientTestDao1.get().insertClientAccount(clientAccount);
        }
        Collections.sort(id);
        ClientFilter clientFilter = new ClientFilter();
        clientFilter.firstname = client.firstname;
        clientFilter.lastname = "";
        clientFilter.patronymic = "";
        clientFilter.orderBy = "id";
        clientFilter.recordSize = 100;
        clientFilter.page = 0;
        clientFilter.recordTotal = 100;
        clientFilter.sort = true;

//
//
        ClientRecord clientRecord = new ClientRecord();
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(clientFilter);
        System.err.println(clientRecords);
        System.err.println(clientRecords.size());
//
//

        assertThat(clientRecords.size()).isEqualTo(2);
        for (int i = 0; i < 2; i++) {
            assertThat(clientRecords.get(i).id).isEqualTo(id.get(i));
            assertThat(clientRecords.get(i).firstname).isEqualTo("Nazar");
        }
    }

    @Test
    public void testGetClientListSort() {

        Charm charm;
        Client client = new Client();
        ClientAccount clientAccount;
        List<Integer> id= new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            charm = addCharm();
            client = addClient(charm);
            id.add(client.id);
            clientAccount = addClientAccount(client);
            clientTestDao1.get().insertCharm(charm);
            clientTestDao1.get().insertClient(client);
            clientTestDao1.get().insertClientAccount(clientAccount);
            clientAccount.money = clientAccount.money + 50;
            clientAccount.id = clientAccount.id + 60;
            clientTestDao1.get().insertClientAccount(clientAccount);
        }
        Collections.sort(id);
        ClientFilter clientFilter = new ClientFilter();
        clientFilter.firstname = "";
        clientFilter.lastname = "";
        clientFilter.patronymic = "";
        clientFilter.orderBy = "id";
        clientFilter.recordSize = 100;
        clientFilter.page = 0;
        clientFilter.recordTotal = 100;
        clientFilter.sort = false;

//
//
        ClientRecord clientRecord = new ClientRecord();
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(clientFilter);
        System.err.println(clientRecords);
        System.err.println(clientRecords.size());
//
//

        assertThat(clientRecords.size()).isEqualTo(12);
        int k=11;
        for (int i = 0; i < 12; i++) {
            assertThat(clientRecords.get(i).id).isEqualTo(id.get(k));
            k--;
        }
    }

    @Test
    public void testGetClientListRecordSize() {

        Charm charm;
        Client client = new Client();
        ClientAccount clientAccount;
        List<Integer> id;
        id = addDatasDao(10);
        Collections.sort(id);
        ClientFilter clientFilter = new ClientFilter();
        clientFilter.firstname = "";
        clientFilter.lastname = "";
        clientFilter.patronymic = "";
        // TODO: asset 9/21/18 mozhesh ubrat ordering po id
        clientFilter.orderBy = "id";
        clientFilter.recordSize = 2;
        clientFilter.page = 0;
        clientFilter.recordTotal = 100;
        clientFilter.sort = false;

//
//
        ClientRecord clientRecord = new ClientRecord();
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(clientFilter);
        System.err.println(clientRecords);
        System.err.println(clientRecords.size());
//
//

        assertThat(clientRecords.size()).isEqualTo(2);
        int k=9;
        for (int i = 0; i < 2; i++) {
            assertThat(clientRecords.get(i).id).isEqualTo(id.get(k));
            k--;
        }
    }

    @Test
    public void testGetClientTotalRecord() {


        addDatasDao(10);
        ClientFilter clientFilter = new ClientFilter();
        clientFilter.firstname = "";
        clientFilter.lastname = "";
        clientFilter.patronymic = "";
        clientFilter.orderBy = "charm";
        clientFilter.recordSize = 10;
        clientFilter.page = 0;
        clientFilter.recordTotal = 100;
        clientFilter.sort = false;
        //
        //
        //
        clientFilter.recordTotal = clientRegister.get().getClientTotalRecord(clientFilter);
        System.err.println("count: " + clientFilter.recordTotal);
        //
        //
        //
        assertThat(clientFilter.recordTotal).isEqualTo(10);
    }

    @Test
    public void testGetClientTotalRecordFilter() {

        Charm charm = new Charm();
        Client client = new Client();
        ClientAccount clientAccount = new ClientAccount();
        for (int i = 0; i < 10; i++) {
            charm = addCharm();
            client = addClient(charm);
            if (i >= 8)
                client.firstname = "Nazar";
            client.lastname = "nazar";
            clientAccount = addClientAccount(client);
            clientTestDao1.get().insertCharm(charm);
            clientTestDao1.get().insertClient(client);
            clientTestDao1.get().insertClientAccount(clientAccount);
            clientAccount.money = clientAccount.money + 50;
            clientAccount.id = clientAccount.id + 50;
            clientTestDao1.get().insertClientAccount(clientAccount);
        }
        ClientFilter clientFilter = new ClientFilter();
        clientFilter.firstname = client.firstname;
        clientFilter.lastname = client.lastname;
        clientFilter.patronymic = "";
        clientFilter.orderBy = "charm";
        clientFilter.recordSize = 10;
        clientFilter.page = 0;
        clientFilter.recordTotal = 100;
        clientFilter.sort = false;
        //
        //
        //
        clientFilter.recordTotal = clientRegister.get().getClientTotalRecord(clientFilter);
        System.err.println("count: " + clientFilter.recordTotal);
        //
        //
        //
        assertThat(clientFilter.recordTotal).isEqualTo(2);
    }
}