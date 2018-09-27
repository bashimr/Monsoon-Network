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
 * Write your transction processor functions here
 */
const namespace = 'monsoon.apptellect.com';

/**
 * CommitPledgeToCrop
 * @param {monsoon.apptellect.com.tx_commitPledgeToCrop} tx_commitPledgeToCrop
 * @transaction
 */
async function CommitPledgeToCrop(txParams){
    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);
    const pledgeRegistry = await getAssetRegistry(namespace + '.Pledge');
    let pledge = await pledgeRegistry.get(txParams.pledgeId);

    //Update Pledge Statue
    pledge.status = 'COMMITTED';
    await pledgeRegistry.update(pledge);

    //Should only create the new array
    if (crop.cropPledges === undefined || crop.cropPledges.length === 0) {
        crop.cropPledges = new Array();
    }
    //Add Pledge to the Crop
    crop.cropPledges.push(pledge);
    await cropRegistry.update(crop);
}

/**
 * CreateNewCrop
 * @param {monsoon.apptellect.com.tx_createNewCrop} tx_createNewCrop
 * @transaction
 */
async function createNewCrop(txParams){

    if(!txParams.farmer) {
        throw new Error('Invalid Farmer');
    }
    var factory = getFactory();
    var cropRegistry = await getAssetRegistry(namespace + '.Crop');
    var crop = factory.newResource(namespace, 'Crop', txParams.cropId);
    crop.farmer = txParams.farmer;
    crop.typeOfCrop = txParams.typeOfCrop;
}


/**
 * tx_setSingleCropCoordinates
 * @param {monsoon.apptellect.com.tx_setSingleCropCoordinates} tx_setSingleCropCoordinates
 * @transaction
 */
async function tx_setSingleCropCoordinates(txParams){

    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);

    if (crop.cropCoordinates === undefined || crop.cropCoordinates.length === 0) {
        crop.cropCoordinates = new Array();
    }

    crop.cropCoordinates.push(txParams.singleSetCoordinates);
    await cropRegistry.update(crop);
}

/**
 * tx_setMultipleCropCoordinates
 * @param {monsoon.apptellect.com.tx_setMultipleCropCoordinates} tx_setMultipleCropCoordinates
 * @transaction
 */
async function tx_setMultipleCropCoordinates(txParams){

    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);

    if (crop.cropCoordinates === undefined || crop.cropCoordinates.length === 0) {
        crop.cropCoordinates = new Array();
    }

    crop.cropCoordinates = txParams.cropCoordinates;
    await cropRegistry.update(crop);
}


/**
 * tx_addPhotoToCrop
 * @param {monsoon.apptellect.com.tx_addPhotoToCrop} tx_addPhotoToCrop
 * @transaction
 */
async function tx_addPhotoToCrop(txParams){

    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);

    if (crop.cropPhotos === undefined || crop.cropPhotos.length === 0) {
        crop.cropCoordinates = new Array();
    }

    crop.cropPhotos.push(txParams.newPhoto);
    await cropRegistry.update(crop);
}


/**
 * tx_setCropStaus
 * @param {monsoon.apptellect.com.tx_setCropStaus} tx_setCropStaus
 * @transaction
 */
async function tx_setCropStaus(txParams){

    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);

    crop.status = txParams.newStatus;
    await cropRegistry.update(crop);
}

//----------------------

/**
 * tx_setCropArea
 * @param {monsoon.apptellect.com.tx_setCropArea} tx_setCropArea
 * @transaction
 */
async function tx_setCropArea(txParams){

    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);

    crop.cropArea = txParams.newArea;
    await cropRegistry.update(crop);
}

/**
 * tx_setCropMaxYieldValue
 * @param {monsoon.apptellect.com.tx_setCropMaxYieldValue} tx_setCropMaxYieldValue
 * @transaction
 */
async function tx_setCropMaxYieldValue(txParams){

    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);

    crop.maxYieldValue = txParams.newMaxYieldValue;
    await cropRegistry.update(crop);
}

/**
 * tx_setCropCurrency
 * @param {monsoon.apptellect.com.tx_setCropCurrency} tx_setCropCurrency
 * @transaction
 */
async function tx_setCropCurrency(txParams){

    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);

    crop.cropCurrency = txParams.newCropCurrency;
    await cropRegistry.update(crop);
}

/**
 * tx_setCropSeedingDate
 * @param {monsoon.apptellect.com.tx_setCropSeedingDate} tx_setCropSeedingDate
 * @transaction
 */
async function tx_setCropSeedingDate(txParams){

    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);

    crop.seedingDate = txParams.newSeedingDate;
    await cropRegistry.update(crop);
}

/**
 * tx_setCropHarvestDate
 * @param {monsoon.apptellect.com.tx_setCropHarvestDate} tx_setCropHarvestDate
 * @transaction
 */
async function tx_setCropHarvestDate(txParams){

    const cropRegistry = await getAssetRegistry(namespace + '.Crop');
    let crop = await cropRegistry.get(txParams.cropId);

    crop.harvestDate = txParams.newHarvestDate;
    await cropRegistry.update(crop);
}

/**
 * Sample transaction
 * @param {monsoon.apptellect.com.SampleTransaction} sampleTransaction
 * @transaction
 */
async function sampleTransaction(tx) {
    // Save the old value of the asset.
    const oldValue = tx.asset.value;

    // Update the asset with the new value.
    tx.asset.value = tx.newValue;

    // Get the asset registry for the asset.
    const assetRegistry = await getAssetRegistry('monsoon.apptellect.com.SampleAsset');
    // Update the asset in the asset registry.
    await assetRegistry.update(tx.asset);

    // Emit an event for the modified asset.
    let event = getFactory().newEvent('monsoon.apptellect.com', 'SampleEvent');
    event.asset = tx.asset;
    event.oldValue = oldValue;
    event.newValue = tx.newValue;
    emit(event);
}
