/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';
/**
 * Write the unit tests for your transction processor functions here
 */

const AdminConnection = require('composer-admin').AdminConnection;
const BusinessNetworkConnection = require('composer-client').BusinessNetworkConnection;
const { BusinessNetworkDefinition, CertificateUtil, IdCard } = require('composer-common');
const path = require('path');

const chai = require('chai');
chai.should();
chai.use(require('chai-as-promised'));

const namespace = 'monsoon.apptellect.com';
const assetType = 'SampleAsset';
const assetNS = namespace + '.' + assetType;
const participantType = 'SampleParticipant';
const participantNS = namespace + '.' + participantType;

const conceptContactDetalis = 'ContactDetails';
const conceptCoordinates = 'Coordinates';
const conceptPhotos = 'Photo';
const conceptPrecipitation = 'Precipitation';

const participantFarmer = 'Farmer';
const FarmerNS = namespace + '.' + participantFarmer;

const participantDonor = 'Donor';
const DonorNS = namespace + '.' + participantDonor;

const assetCrop = 'Crop';
const CropNS = namespace + '.' + assetCrop;

const assetPledge = 'Pledge';
const PledgeNS = namespace + '.' + assetPledge;

describe('#' + namespace, () => {
    // In-memory card store for testing so cards are not persisted to the file system
    const cardStore = require('composer-common').NetworkCardStoreManager.getCardStore( { type: 'composer-wallet-inmemory' } );

    // Embedded connection used for local testing
    /*
    const connectionProfile = {
        name: 'embedded',
        'x-type': 'embedded'
    };
    */

    // Connection Profile for AWS deployment
    const connectionProfile = {
        name: 'embedded',
        'x-type': 'embedded'
    };

    // Name of the business network card containing the administrative identity for the business network
    const adminCardName = 'admin';

    // Admin connection to the blockchain, used to deploy the business network
    let adminConnection;

    // This is the business network connection the tests will use.
    let businessNetworkConnection;

    // This is the factory for creating instances of types.
    let factory;

    // These are the identities for Alice and Bob.
    const aliceCardName = 'alice';
    const bobCardName = 'bob';
    const farmer1CardName = 'farmer1';

    // These are a list of receieved events.
    let events;

    let businessNetworkName;

    before(async () => {
        // Generate certificates for use with the embedded connection
        const credentials = CertificateUtil.generate({ commonName: 'admin' });

        // Identity used with the admin connection to deploy business networks
        const deployerMetadata = {
            version: 1,
            userName: 'PeerAdmin',
            roles: [ 'PeerAdmin', 'ChannelAdmin' ]
        };
        const deployerCard = new IdCard(deployerMetadata, connectionProfile);
        deployerCard.setCredentials(credentials);
        const deployerCardName = 'PeerAdmin';

        adminConnection = new AdminConnection({ cardStore: cardStore });

        await adminConnection.importCard(deployerCardName, deployerCard);
        await adminConnection.connect(deployerCardName);
    });

    /**
     *
     * @param {String} cardName The card name to use for this identity
     * @param {Object} identity The identity details
     */
    async function importCardForIdentity(cardName, identity) {
        const metadata = {
            userName: identity.userID,
            version: 1,
            enrollmentSecret: identity.userSecret,
            businessNetwork: businessNetworkName
        };
        const card = new IdCard(metadata, connectionProfile);
        await adminConnection.importCard(cardName, card);
    }

    // This is called before each test is executed.
    beforeEach(async () => {
        // Generate a business network definition from the project directory.
        let businessNetworkDefinition = await BusinessNetworkDefinition.fromDirectory(path.resolve(__dirname, '..'));
        businessNetworkName = businessNetworkDefinition.getName();
        await adminConnection.install(businessNetworkDefinition);
        const startOptions = {
            networkAdmins: [
                {
                    userName: 'admin',
                    enrollmentSecret: 'adminpw'
                }
            ]
        };
        const adminCards = await adminConnection.start(businessNetworkName, businessNetworkDefinition.getVersion(), startOptions);
        await adminConnection.importCard(adminCardName, adminCards.get('admin'));

        // Create and establish a business network connection
        businessNetworkConnection = new BusinessNetworkConnection({ cardStore: cardStore });
        events = [];
        businessNetworkConnection.on('event', event => {
            events.push(event);
        });
        await businessNetworkConnection.connect(adminCardName);

        // Get the factory for the business network.
        factory = businessNetworkConnection.getBusinessNetwork().getFactory();

        const participantRegistry = await businessNetworkConnection.getParticipantRegistry(participantNS);
        // Create the participants.
        const alice = factory.newResource(namespace, participantType, 'alice@email.com');
        alice.firstName = 'Alice';
        alice.lastName = 'A';

        const bob = factory.newResource(namespace, participantType, 'bob@email.com');
        bob.firstName = 'Bob';
        bob.lastName = 'B';
        participantRegistry.addAll([alice, bob]);

        //Add a Farmer participant
        const farmerRegistry = await businessNetworkConnection.getParticipantRegistry (FarmerNS);
        const farmer1 = factory.newResource(namespace, participantFarmer, 'farmer1@email.com');
        farmer1.firstName = 'Imran';
        farmer1.lastName = 'Bashir';
        const contact = factory.newConcept (namespace, conceptContactDetalis);
        contact.phoneNumber = '222 222 2222';
        contact.email = 'farmer1@email.com';
        contact.country = 'INDIA';
        farmer1.contactInfo = contact;
        farmerRegistry.addAll([farmer1]);

        //Add a Donor participant
        const donorRegistry = await businessNetworkConnection.getParticipantRegistry(DonorNS);
        const donor1 = factory.newResource(namespace, participantDonor, 'donor1');
        donor1.donorType = 'INDIVIDUAL';
        donorRegistry.addAll([donor1]);

        //Create New Crop Asset
        const cropRegistry = await businessNetworkConnection.getAssetRegistry(CropNS);
        const crop1 = factory.newResource(namespace, assetCrop, 'crop1');
        //Add Coordinates for the crop
        const coordinates1 = factory.newConcept (namespace, conceptCoordinates);
        const coordinates2 = factory.newConcept (namespace, conceptCoordinates);
        const coordinates3 = factory.newConcept (namespace, conceptCoordinates);
        const coordinates4 = factory.newConcept (namespace, conceptCoordinates);
        coordinates1.lattitude = 43.587896;
        coordinates1.longitude = -79.642621;
        let coordinatesArray = [];
        coordinatesArray.push(coordinates1);
        //2nd coordinates
        coordinates2.lattitude = 33.587896;
        coordinates2.longitude = -69.642621;
        coordinatesArray.push(coordinates2);
        //3rd coordinates
        coordinates3.lattitude = 23.587896;
        coordinates3.longitude = -59.642621;
        coordinatesArray.push(coordinates3);
        //4th coordinates
        coordinates4.lattitude = 13.587896;
        coordinates4.longitude = -49.642621;
        coordinatesArray.push(coordinates4);
        crop1.cropCoordinates = coordinatesArray;

        crop1.typeOfCrop = 'COTTON';

        //Add Photos
        const photoCoords = factory.newConcept (namespace, conceptCoordinates);
        const photo = factory.newConcept(namespace, conceptPhotos);
        photo.location = 'www.apptellect.com';

        photoCoords.lattitude = 3.587896;
        photoCoords.longitude = -39.642621;
        photo.photoCoordinates = photoCoords;
        crop1.cropPhotos = [photo];

        crop1.cropArea = 1.35;
        crop1.maxYieldValue = 15835.24;
        crop1.seedingDate = new Date('December 18, 2018');
        crop1.harvestDate = new Date('July 20, 2018');
        crop1.farmerOwner = factory.newRelationship(namespace, participantFarmer, 'farmer1');

        const precipitation = factory.newConcept(namespace, conceptPrecipitation);
        precipitation.date = new Date();
        precipitation.value = 1.2;

        crop1.precipitationHistory = [precipitation];

        //Add the Crop Assets assets
        cropRegistry.addAll([crop1]);

        //Add 2 Pledge Assets for one donor
        const pledgeRegistry = await businessNetworkConnection.getAssetRegistry(PledgeNS);

        const pledge1 =factory.newResource(namespace, assetPledge, 'pledge1');
        pledge1.amount = 2322.35;
        pledge1.donor = factory.newRelationship(namespace, participantDonor, 'donor1');

        const pledge2 =factory.newResource(namespace, assetPledge, 'pledge2');
        pledge2.amount = 1000.01;
        pledge2.donor = factory.newRelationship(namespace, participantDonor, 'donor1');

        pledgeRegistry.addAll([pledge1, pledge2]);

        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        // Create the assets.
        const asset1 = factory.newResource(namespace, assetType, '1');
        asset1.owner = factory.newRelationship(namespace, participantType, 'alice@email.com');
        asset1.value = '10';

        const asset2 = factory.newResource(namespace, assetType, '2');
        asset2.owner = factory.newRelationship(namespace, participantType, 'bob@email.com');
        asset2.value = '20';

        assetRegistry.addAll([asset1, asset2]);

        // Issue the identities.
        let identity = await businessNetworkConnection.issueIdentity(participantNS + '#alice@email.com', 'alice1');
        await importCardForIdentity(aliceCardName, identity);
        identity = await businessNetworkConnection.issueIdentity(participantNS + '#bob@email.com', 'bob1');
        await importCardForIdentity(bobCardName, identity);
    });

    /**
     * Reconnect using a different identity.
     * @param {String} cardName The name of the card for the identity to use
     */
    async function useIdentity(cardName) {
        await businessNetworkConnection.disconnect();
        businessNetworkConnection = new BusinessNetworkConnection({ cardStore: cardStore });
        events = [];
        businessNetworkConnection.on('event', (event) => {
            events.push(event);
        });
        await businessNetworkConnection.connect(cardName);
        factory = businessNetworkConnection.getBusinessNetwork().getFactory();
    }

    it('Farmer1 is added successfully', async () => {
        await useIdentity (adminCardName);
        const farmerRegistry = await businessNetworkConnection.getParticipantRegistry(FarmerNS);
        const farmers = await farmerRegistry.getAll();

        //Validate Farmer Participatn
        farmers.should.have.lengthOf(1);
        const farmer1 = farmers[0];
        farmer1.firstName.should.equal('Imran');
        farmer1.lastName.should.equal('Bashir');
        farmer1.contactInfo.phoneNumber.should.equal('222 222 2222');
        farmer1.contactInfo.email.should.equal('farmer1@email.com');
        farmer1.contactInfo.country.should.equal('INDIA');
    });

    it('Crop 1 should be added successfully', async () => {
        await useIdentity(adminCardName);
        const cropRegistry = await businessNetworkConnection.getAssetRegistry(CropNS);
        const crops = await cropRegistry.getAll();
        crops.should.have.lengthOf(1);
        const crop1 = crops[0];
        crop1.cropCoordinates.should.have.lengthOf(4);

        for (var i = 0, len = crop1.cropCoordinates.length; i < len; i++) {
            console.log(crop1.cropCoordinates[i].lattitude);
            console.log(crop1.cropCoordinates[i].longitude);
        }

        crop1.typeOfCrop.should.equal('COTTON');
        crop1.cropPhotos[0].location.should.equal('www.apptellect.com');
        console.log(crop1.cropPhotos[0].location);
        console.log(crop1.cropPhotos[0].photoCoordinates.lattitude);
        console.log(crop1.cropPhotos[0].photoCoordinates.longitude);
        console.log(crop1.seedingDate);
        crop1.farmerOwner.getFullyQualifiedIdentifier().should.equal(FarmerNS + '#farmer1');
    });

    it('Donor participant donor1 is added successfully', async() => {
        await useIdentity (adminCardName);
        const donorRegistry = await businessNetworkConnection.getParticipantRegistry(DonorNS);
        const donors = await donorRegistry.getAll();

        donors.should.have.lengthOf(1);
        const donor1 = donors[0];
        donor1.donorType.should.equal('INDIVIDUAL');
    });

    it('Pledge asset pledge1 added successfully', async() => {
        await useIdentity(adminCardName);
        const pledgeRegistry = await businessNetworkConnection.getAssetRegistry(PledgeNS);
        const pledges = await pledgeRegistry.getAll();

        pledges.should.have.lengthOf(2);
        pledges[0].amount.should.equal(2322.35);
        pledges[0].pledgeCurrency.should.equal('US_DOLLAR');
        pledges[0].status.should.equal('NONCOMMITTED');
        pledges[0].donor.getFullyQualifiedIdentifier().should.equal(DonorNS + '#donor1');

        pledges[1].amount.should.equal(1000.01);
    });

    it('Admin can Submit a transaction to Commit the pledge to a Crop', async () => {
        // Use the identity for Alice.
        await useIdentity(adminCardName);

        // Submit the transaction
        const transaction = factory.newTransaction(namespace, 'tx_commitPledgeToCrop');
        transaction.cropId = 'crop1';
        transaction.pledgeId = 'pledge1';
        await businessNetworkConnection.submitTransaction(transaction);

        const transaction2 = factory.newTransaction(namespace, 'tx_commitPledgeToCrop');
        transaction2.cropId = 'crop1';
        transaction2.pledgeId = 'pledge2';
        await businessNetworkConnection.submitTransaction(transaction2);

        // Validate that the pledge has been committed to the Crop
        const cropRegistry = await businessNetworkConnection.getAssetRegistry(CropNS);
        const crop1 = await cropRegistry.get('crop1');
        crop1.cropPledges.should.have.lengthOf(2);
        crop1.cropPledges[0].getFullyQualifiedIdentifier().should.equal(PledgeNS + '#pledge1');
        crop1.cropPledges[1].getFullyQualifiedIdentifier().should.equal(PledgeNS + '#pledge2');

        // Validate the the pledge status has been updated.
        const pledgeRegistry = await businessNetworkConnection.getAssetRegistry(PledgeNS);
        const pledge1 = await pledgeRegistry.get('pledge1');
        pledge1.status.should.equal('COMMITTED');
    });

    it('Get transaction registry', async () => {

        await useIdentity (adminCardName);
        const tranRegistries = await businessNetworkConnection.getAllTransactionRegistries(true);
        //console.log(tranRegistries);
        for (var i = 0, len = tranRegistries.length; i < len; i++) {
            console.log(tranRegistries[i].id);
        }

        /* The following block is to see if we actually see the transactions in transaction registry
        const transaction = factory.newTransaction(namespace, 'commitPledgeToCrop');
        transaction.cropId = 'crop1';
        transaction.pledgeId = 'pledge1';
        await businessNetworkConnection.submitTransaction(transaction);

        const transaction2 = factory.newTransaction(namespace, 'commitPledgeToCrop');
        transaction2.cropId = 'crop1';
        transaction2.pledgeId = 'pledge2';
        await businessNetworkConnection.submitTransaction(transaction2);
        */

        const tranRegistry = await businessNetworkConnection.getAssetRegistry('monsoon.apptellect.com.commitPledgeToCrop');
        const trans = await tranRegistry.getAll();
        console.log(trans);
    });

    it.only('Get List of Economic Models', async () => {

        await useIdentity (adminCardName);
        //const transaction = factory.newTransaction(namespace, 'tx_getEconomicModels');
        //await businessNetworkConnection.submitTransaction(transaction);
        const assetRegistries = await businessNetworkConnection.getAllAssetRegistries();
        for (var i = 0, len = assetRegistries.length; i < len; i++) {
            console.log(assetRegistries[i].id);
        }
    });

    it('Test if we can add a new Crop asset by submitting a transaction', async () => {
        // Use the identity for Alice.
        await useIdentity(adminCardName);

        // Submit the transaction
        const transaction = factory.newTransaction(namespace, 'tx_createNewCrop');
        transaction.cropId = 'crop2';
        transaction.typeOfCrop = 'SUGARCANE';
        transaction.farmer = factory.newRelationship(namespace, participantFarmer, 'farmer1');
        await businessNetworkConnection.submitTransaction(transaction);

    });

    it('Test if we can add single coordinate to the existig crop', async () => {
        await useIdentity(adminCardName);

        // Submit the transaction
        const transaction = factory.newTransaction(namespace, 'tx_setSingleCropCoordinates');
        transaction.cropId = 'crop1';

        const coordinates = factory.newConcept (namespace, conceptCoordinates);
        coordinates.lattitude = 3.587896;
        coordinates.longitude = -39.642621;

        transaction.singleSetCoordinates = coordinates;
        await businessNetworkConnection.submitTransaction(transaction);

        //Verify that the coordinates were added

        const cropRegistry = await businessNetworkConnection.getAssetRegistry(CropNS);
        const crop = await cropRegistry.get('crop1');
        crop.cropCoordinates.should.have.lengthOf(5);

        for (var i = 0, len = crop.cropCoordinates.length; i < len; i++) {
            console.log(crop.cropCoordinates[i].lattitude);
            console.log(crop.cropCoordinates[i].longitude);
        }

    });

    it('Test if we can set multiple coordinated to an existing crop', async () => {
        const transaction = factory.newTransaction(namespace, 'tx_setMultipleCropCoordinates');
        transaction.cropId = 'crop1';

        var coordinatesArray = [];

        for (var i = 0; i < 4; i++) {
            coordinatesArray[i] = factory.newConcept (namespace, conceptCoordinates);
            coordinatesArray[i].lattitude = 3.587896 + 10 * i;
            coordinatesArray[i].longitude = -39.642621 - 10 * i;
        }

        transaction.cropCoordinates = coordinatesArray;
        await businessNetworkConnection.submitTransaction(transaction);

        //Verify that the coordinates were added

        const cropRegistry = await businessNetworkConnection.getAssetRegistry(CropNS);
        const crop = await cropRegistry.get('crop1');
        crop.cropCoordinates.should.have.lengthOf(4);
        for (i = 0 ; i < crop.cropCoordinates.length; i++) {
            console.log(crop.cropCoordinates[i].lattitude);
            console.log(crop.cropCoordinates[i].longitude);
        }

    });

    it('Check if the new Photo can be added to the crop', async()=> {

        const transaction = factory.newTransaction(namespace, 'tx_addPhotoToCrop');
        transaction.cropId = 'crop1';

        const photoCoords = factory.newConcept (namespace, conceptCoordinates);
        const photo = factory.newConcept(namespace, conceptPhotos);
        photo.location = 'www.photo2.com';

        photoCoords.lattitude = 3.587896;
        photoCoords.longitude = -29.642621;
        photo.photoCoordinates = photoCoords;
        transaction.newPhoto = photo;

        await businessNetworkConnection.submitTransaction(transaction);

        //Verify that the coordinates were added

        const cropRegistry = await businessNetworkConnection.getAssetRegistry(CropNS);
        const crop1 = await cropRegistry.get('crop1');

        for (let i = 0 ; i < crop1.cropPhotos.length; i++) {
            console.log(crop1.cropPhotos[i].location);
            console.log(crop1.cropPhotos[i].photoCoordinates.lattitude);
            console.log(crop1.cropPhotos[i].photoCoordinates.longitude);
        }
    });

    it('Check if the CropStatus, crop Area, maxYieldValue, cropCurrency, seedingDate, HarvestDate gets updated successfully', async () =>{

        //Status update transaction
        const transactionStatus = factory.newTransaction(namespace, 'tx_setCropStaus');
        transactionStatus.cropId = 'crop1';
        transactionStatus.newStatus = 'SEEDED';
        await businessNetworkConnection.submitTransaction(transactionStatus);
        //Area update transaction
        const transactionArea = factory.newTransaction(namespace, 'tx_setCropArea');
        transactionArea.cropId = 'crop1';
        transactionArea.newArea = 3.7;
        await businessNetworkConnection.submitTransaction(transactionArea);
        //maxYieldValue update transaction
        const transactionYieldValue = factory.newTransaction(namespace, 'tx_setCropMaxYieldValue');
        transactionYieldValue.cropId = 'crop1';
        transactionYieldValue.newMaxYieldValue = 35000.35;
        await businessNetworkConnection.submitTransaction(transactionYieldValue);
        //cropCurrency update transaction
        const transactionCurrency = factory.newTransaction(namespace, 'tx_setCropCurrency');
        transactionCurrency.cropId = 'crop1';
        transactionCurrency.newCropCurrency = 'PAKISTANI_RUPEE';
        await businessNetworkConnection.submitTransaction(transactionCurrency);
        //seedingDate update tranasction
        const transactionSeeding = factory.newTransaction(namespace, 'tx_setCropSeedingDate');
        transactionSeeding.cropId = 'crop1';
        transactionSeeding.newSeedingDate = new Date('July 20, 2018');
        await businessNetworkConnection.submitTransaction(transactionSeeding);
        //harvestDate update transaction
        const transactionHarvest = factory.newTransaction(namespace, 'tx_setCropHarvestDate');
        transactionHarvest.cropId = 'crop1';
        transactionHarvest.newHarvestDate = new Date ('July 30, 2019');
        await businessNetworkConnection.submitTransaction(transactionHarvest);

        //Verify that the status was updated successfully

        const cropRegistry = await businessNetworkConnection.getAssetRegistry(CropNS);
        const crop1 = await cropRegistry.get('crop1');
        crop1.status.should.equal('SEEDED');
        crop1.cropArea.should.equal(3.7);
        crop1.maxYieldValue.should.equal(35000.35);
        crop1.cropCurrency.should.equal('PAKISTANI_RUPEE');
        crop1.seedingDate.getTime().should.equal(new Date('July 20, 2018').getTime());
        crop1.harvestDate.getTime().should.equal(new Date ('July 30, 2019').getTime());
        console.log(crop1.harvestDate.getTime());
    });
/*
    it('Alice can read all of the assets', async () => {
        // Use the identity for Alice.
        await useIdentity(aliceCardName);
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        const assets = await assetRegistry.getAll();

        // Validate the assets.
        assets.should.have.lengthOf(2);
        const asset1 = assets[0];
        asset1.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#alice@email.com');
        asset1.value.should.equal('10');
        const asset2 = assets[1];
        asset2.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#bob@email.com');
        asset2.value.should.equal('20');
    });

    it('Bob can read all of the assets', async () => {
        // Use the identity for Bob.
        await useIdentity(bobCardName);
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        const assets = await assetRegistry.getAll();

        // Validate the assets.
        assets.should.have.lengthOf(2);
        const asset1 = assets[0];
        asset1.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#alice@email.com');
        asset1.value.should.equal('10');
        const asset2 = assets[1];
        asset2.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#bob@email.com');
        asset2.value.should.equal('20');
    });

    it('Alice can add assets that she owns', async () => {
        // Use the identity for Alice.
        await useIdentity(aliceCardName);

        // Create the asset.
        let asset3 = factory.newResource(namespace, assetType, '3');
        asset3.owner = factory.newRelationship(namespace, participantType, 'alice@email.com');
        asset3.value = '30';

        // Add the asset, then get the asset.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        await assetRegistry.add(asset3);

        // Validate the asset.
        asset3 = await assetRegistry.get('3');
        asset3.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#alice@email.com');
        asset3.value.should.equal('30');
    });

    it('Alice cannot add assets that Bob owns', async () => {
        // Use the identity for Alice.
        await useIdentity(aliceCardName);

        // Create the asset.
        const asset3 = factory.newResource(namespace, assetType, '3');
        asset3.owner = factory.newRelationship(namespace, participantType, 'bob@email.com');
        asset3.value = '30';

        // Try to add the asset, should fail.
        const assetRegistry = await  businessNetworkConnection.getAssetRegistry(assetNS);
        assetRegistry.add(asset3).should.be.rejectedWith(/does not have .* access to resource/);
    });

    it('Bob can add assets that he owns', async () => {
        // Use the identity for Bob.
        await useIdentity(bobCardName);

        // Create the asset.
        let asset4 = factory.newResource(namespace, assetType, '4');
        asset4.owner = factory.newRelationship(namespace, participantType, 'bob@email.com');
        asset4.value = '40';

        // Add the asset, then get the asset.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        await assetRegistry.add(asset4);

        // Validate the asset.
        asset4 = await assetRegistry.get('4');
        asset4.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#bob@email.com');
        asset4.value.should.equal('40');
    });

    it('Bob cannot add assets that Alice owns', async () => {
        // Use the identity for Bob.
        await useIdentity(bobCardName);

        // Create the asset.
        const asset4 = factory.newResource(namespace, assetType, '4');
        asset4.owner = factory.newRelationship(namespace, participantType, 'alice@email.com');
        asset4.value = '40';

        // Try to add the asset, should fail.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        assetRegistry.add(asset4).should.be.rejectedWith(/does not have .* access to resource/);

    });

    it('Alice can update her assets', async () => {
        // Use the identity for Alice.
        await useIdentity(aliceCardName);

        // Create the asset.
        let asset1 = factory.newResource(namespace, assetType, '1');
        asset1.owner = factory.newRelationship(namespace, participantType, 'alice@email.com');
        asset1.value = '50';

        // Update the asset, then get the asset.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        await assetRegistry.update(asset1);

        // Validate the asset.
        asset1 = await assetRegistry.get('1');
        asset1.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#alice@email.com');
        asset1.value.should.equal('50');
    });

    it('Alice cannot update Bob\'s assets', async () => {
        // Use the identity for Alice.
        await useIdentity(aliceCardName);

        // Create the asset.
        const asset2 = factory.newResource(namespace, assetType, '2');
        asset2.owner = factory.newRelationship(namespace, participantType, 'bob@email.com');
        asset2.value = '50';

        // Try to update the asset, should fail.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        assetRegistry.update(asset2).should.be.rejectedWith(/does not have .* access to resource/);
    });

    it('Bob can update his assets', async () => {
        // Use the identity for Bob.
        await useIdentity(bobCardName);

        // Create the asset.
        let asset2 = factory.newResource(namespace, assetType, '2');
        asset2.owner = factory.newRelationship(namespace, participantType, 'bob@email.com');
        asset2.value = '60';

        // Update the asset, then get the asset.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        await assetRegistry.update(asset2);

        // Validate the asset.
        asset2 = await assetRegistry.get('2');
        asset2.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#bob@email.com');
        asset2.value.should.equal('60');
    });

    it('Bob cannot update Alice\'s assets', async () => {
        // Use the identity for Bob.
        await useIdentity(bobCardName);

        // Create the asset.
        const asset1 = factory.newResource(namespace, assetType, '1');
        asset1.owner = factory.newRelationship(namespace, participantType, 'alice@email.com');
        asset1.value = '60';

        // Update the asset, then get the asset.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        assetRegistry.update(asset1).should.be.rejectedWith(/does not have .* access to resource/);

    });

    it('Alice can remove her assets', async () => {
        // Use the identity for Alice.
        await useIdentity(aliceCardName);

        // Remove the asset, then test the asset exists.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        await assetRegistry.remove('1');
        const exists = await assetRegistry.exists('1');
        exists.should.be.false;
    });

    it('Alice cannot remove Bob\'s assets', async () => {
        // Use the identity for Alice.
        await useIdentity(aliceCardName);

        // Remove the asset, then test the asset exists.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        assetRegistry.remove('2').should.be.rejectedWith(/does not have .* access to resource/);
    });

    it('Bob can remove his assets', async () => {
        // Use the identity for Bob.
        await useIdentity(bobCardName);

        // Remove the asset, then test the asset exists.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        await assetRegistry.remove('2');
        const exists = await assetRegistry.exists('2');
        exists.should.be.false;
    });

    it('Bob cannot remove Alice\'s assets', async () => {
        // Use the identity for Bob.
        await useIdentity(bobCardName);

        // Remove the asset, then test the asset exists.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        assetRegistry.remove('1').should.be.rejectedWith(/does not have .* access to resource/);
    });

    it('Alice can submit a transaction for her assets', async () => {
        // Use the identity for Alice.
        await useIdentity(aliceCardName);

        // Submit the transaction.
        const transaction = factory.newTransaction(namespace, 'SampleTransaction');
        transaction.asset = factory.newRelationship(namespace, assetType, '1');
        transaction.newValue = '50';
        await businessNetworkConnection.submitTransaction(transaction);

        // Get the asset.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        const asset1 = await assetRegistry.get('1');

        // Validate the asset.
        asset1.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#alice@email.com');
        asset1.value.should.equal('50');

        // Validate the events.
        events.should.have.lengthOf(1);
        const event = events[0];
        event.eventId.should.be.a('string');
        event.timestamp.should.be.an.instanceOf(Date);
        event.asset.getFullyQualifiedIdentifier().should.equal(assetNS + '#1');
        event.oldValue.should.equal('10');
        event.newValue.should.equal('50');
    });

    it('Alice cannot submit a transaction for Bob\'s assets', async () => {
        // Use the identity for Alice.
        await useIdentity(aliceCardName);

        // Submit the transaction.
        const transaction = factory.newTransaction(namespace, 'SampleTransaction');
        transaction.asset = factory.newRelationship(namespace, assetType, '2');
        transaction.newValue = '50';
        businessNetworkConnection.submitTransaction(transaction).should.be.rejectedWith(/does not have .* access to resource/);
    });

    it('Bob can submit a transaction for his assets', async () => {
        // Use the identity for Bob.
        await useIdentity(bobCardName);

        // Submit the transaction.
        const transaction = factory.newTransaction(namespace, 'SampleTransaction');
        transaction.asset = factory.newRelationship(namespace, assetType, '2');
        transaction.newValue = '60';
        await businessNetworkConnection.submitTransaction(transaction);

        // Get the asset.
        const assetRegistry = await businessNetworkConnection.getAssetRegistry(assetNS);
        const asset2 = await assetRegistry.get('2');

        // Validate the asset.
        asset2.owner.getFullyQualifiedIdentifier().should.equal(participantNS + '#bob@email.com');
        asset2.value.should.equal('60');

        // Validate the events.
        events.should.have.lengthOf(1);
        const event = events[0];
        event.eventId.should.be.a('string');
        event.timestamp.should.be.an.instanceOf(Date);
        event.asset.getFullyQualifiedIdentifier().should.equal(assetNS + '#2');
        event.oldValue.should.equal('20');
        event.newValue.should.equal('60');
    });

    it('Bob cannot submit a transaction for Alice\'s assets', async () => {
        // Use the identity for Bob.
        await useIdentity(bobCardName);

        // Submit the transaction.
        const transaction = factory.newTransaction(namespace, 'SampleTransaction');
        transaction.asset = factory.newRelationship(namespace, assetType, '1');
        transaction.newValue = '60';
        businessNetworkConnection.submitTransaction(transaction).should.be.rejectedWith(/does not have .* access to resource/);
    });
*/
});
