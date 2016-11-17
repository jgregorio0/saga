//var jsonUrl = "../resources/json/anonymize.json";
var jsonUrl = "../resources/json/anonymize-random.json";

var DATEINC = 100;
var PROFILENAME = "DemiliTalemProfile";
var PROJECTNAME = "DemiliTalemProject";
var SHORTNAME = "DemTalPrj";
var SITEID = "123456";
var SITENAME = "DemiliTalem";
var TRIALNAME = "TrialName";
var TRIALSPONSOR = "TrialSponsor";
var UIDROOT = "";


var attrs = ["x00080005","x00080008","x00080012","x00080013","x00080014","x00080016","x00080018","x00080020","x00080021","x00080022","x00080023","x00080024","x00080025","x0008002a","x00080030","x00080031","x00080032","x00080033","x00080034","x00080035","x00080050","x00080052","x00080054","x00080056","x00080058","x00080060","x00080061","x00080064","x00080068","x00080070","x00080080","x00080081","x00080082","x00080090","x00080092","x00080094","x00080096","x00080100","x00080102","x00080103","x00080104","x00080105","x00080106","x00080107","x0008010b","x0008010c","x0008010d","x0008010f","x00080201","x00081010","x00081030","x00081032","x0008103e","x00081040","x00081048","x00081049","x00081050","x00081052","x00081060","x00081062","x00081070","x00081072","x00081080","x00081084","x00081090","x00081100","x00081110","x00081111","x00081115","x00081120","x00081125","x00081130","x00081140","x00081145","x0008114a","x00081150","x00081155","x0008115a","x00081160","x00081195","x00081197","x00081198","x00081199","x00082111","x00082112","x00082120","x00082122","x00082124","x00082128","x00082129","x0008212a","x00082130","x00082132","x00082142","x00082143","x00082144","x00082218","x00082220","x00082228","x00082229","x00082230","x00082240","x00082242","x00082244","x00082246","x00083010","x00084000","x00089007","x00089092","x00089121","x00089123","x00089124","x00089154","x00089205","x00089206","x00089207","x00089208","x00089209","x00089215","x00089237","x00100010","x00100020","x00100021","x00100030","x00100032","x00100040","x00100050","x00100101","x00100102","x00101000","x00101001","x00101002","x00101005","x00101010","x00101020","x00101030","x00101040","x00101050","x00101060","x00101080","x00101081","x00101090","x00102000","x00102110","x00102150","x00102152","x00102154","x00102160","x00102180","x001021a0","x001021b0","x001021c0","x001021d0","x001021f0","x00102203","x00102297","x00102299","x00104000","x00120010","x00120020","x00120021","x00120030","x00120031","x00120040","x00120042","x00120050","x00120051","x00120060","x00120062","x00120063","x00120064","x00130010","x00131010","x00131011","x00131012","x00131013","x00180010","x00180012","x00180014","x00180015","x00180020","x00180021","x00180022","x00180023","x00180024","x00180025","x00180026","x00180027","x00180028","x00180029","x0018002a","x00180031","x00180034","x00180035","x00180036","x00180037","x00180038","x00180039","x00180040","x00180050","x00180060","x00180070","x00180071","x00180072","x00180073","x00180074","x00180075","x00180080","x00180081","x00180082","x00180083","x00180084","x00180085","x00180086","x00180087","x00180088","x00180089","x00180090","x00180091","x00180093","x00180094","x00180095","x00181000","x00181002","x00181004","x00181005","x00181007","x00181008","x00181010","x00181011","x00181012","x00181014","x00181016","x00181017","x00181018","x00181019","x0018101a","x0018101b","x00181020","x00181022","x00181023","x00181030","x00181040","x00181041","x00181042","x00181043","x00181044","x00181045","x00181046","x00181047","x00181048","x00181049","x00181050","x00181060","x00181061","x00181062","x00181063","x00181064","x00181065","x00181066","x00181067","x00181068","x00181069","x0018106a","x0018106c","x0018106e","x00181070","x00181071","x00181072","x00181073","x00181074","x00181075","x00181076","x00181077","x00181080","x00181081","x00181082","x00181083","x00181084","x00181085","x00181086","x00181088","x00181090","x00181094","x00181100","x00181110","x00181111","x00181114","x00181120","x00181121","x00181130","x00181131","x00181134","x00181135","x00181136","x00181137","x00181138","x0018113a","x00181140","x00181141","x00181142","x00181143","x00181144","x00181145","x00181147","x00181149","x00181150","x00181151","x00181152","x00181153","x00181154","x00181155","x00181156","x0018115a","x0018115e","x00181160","x00181161","x00181162","x00181164","x00181166","x00181170","x00181180","x00181181","x00181182","x00181183","x00181184","x00181190","x00181191","x001811a0","x001811a2","x00181200","x00181201","x00181210","x00181242","x00181243","x00181244","x00181250","x00181251","x00181260","x00181261","x00181300","x00181301","x00181302","x00181310","x00181312","x00181314","x00181315","x00181316","x00181318","x00181400","x00181401","x00181402","x00181403","x00181404","x00181405","x00181450","x00181460","x00181470","x00181480","x00181490","x00181491","x00181495","x00181500","x00181508","x00181510","x00181511","x00181520","x00181521","x00181530","x00181531","x00181600","x00181602","x00181604","x00181606","x00181608","x00181610","x00181612","x00181620","x00181622","x00181623","x00181700","x00181702","x00181704","x00181706","x00181708","x00181710","x00181712","x00181720","x00181800","x00181801","x00181802","x00184000","x00185000","x00185010","x00185012","x00185020","x00185021","x00185022","x00185024","x00185026","x00185027","x00185028","x00185029","x00185050","x00185100","x00185101","x00185104","x00185210","x00185212","x00186000","x00186011","x00186012","x00186014","x00186016","x00186018","x0018601a","x0018601c","x0018601e","x00186020","x00186022","x00186024","x00186026","x00186028","x0018602a","x0018602c","x0018602e","x00186030","x00186031","x00186032","x00186034","x00186036","x00186038","x0018603a","x0018603c","x0018603e","x00186040","x00186042","x00186044","x00186046","x00186048","x0018604a","x0018604c","x0018604e","x00186050","x00186052","x00186054","x00186056","x00186058","x0018605a","x00187000","x00187001","x00187004","x00187005","x00187006","x00187008","x0018700a","x0018700c","x0018700e","x00187010","x00187011","x00187012","x00187014","x00187016","x0018701a","x00187020","x00187022","x00187024","x00187026","x00187028","x00187030","x00187032","x00187034","x00187040","x00187041","x00187042","x00187044","x00187046","x00187048","x0018704c","x00187050","x00187052","x00187054","x00187060","x00187062","x00187064","x00187065","x00188150","x00188151","x00189004","x00189005","x00189006","x00189008","x00189009","x00189010","x00189011","x00189012","x00189014","x00189015","x00189016","x00189017","x00189018","x00189019","x00189020","x00189021","x00189022","x00189024","x00189025","x00189026","x00189027","x00189028","x00189029","x00189030","x00189032","x00189033","x00189034","x00189035","x00189036","x00189037","x00189041","x00189042","x00189043","x00189044","x00189045","x00189046","x00189047","x00189048","x00189049","x00189050","x00189051","x00189052","x00189053","x00189054","x00189058","x00189059","x00189060","x00189061","x00189062","x00189063","x00189064","x00189065","x00189066","x00189067","x00189070","x00189073","x00189074","x00189075","x00189076","x00189077","x00189078","x00189079","x00189080","x00189081","x00189082","x00189084","x00189085","x00189087","x00189089","x00189090","x00189091","x00189093","x00189094","x00189095","x00189096","x00189098","x00189100","x00189101","x00189103","x00189104","x00189105","x00189106","x00189107","x00189112","x00189114","x00189115","x00189117","x00189118","x00189119","x00189125","x00189126","x00189127","x00189147","x00189151","x00189152","x00189155","x00189159","x00189166","x00189168","x00189169","x00189170","x00189171","x00189172","x00189173","x00189174","x00189175","x00189176","x00189177","x00189178","x00189179","x00189180","x00189181","x00189182","x00189183","x00189184","x00189195","x00189196","x00189197","x00189198","x00189199","x00189200","x00189214","x00189217","x00189218","x00189219","x00189220","x00189226","x00189227","x00189231","x00189232","x00189234","x00189236","x00189239","x00189424","x0018a003","x0020000d","x0020000e","x00200010","x00200011","x00200012","x00200013","x00200019","x00200020","x00200022","x00200024","x00200026","x00200032","x00200037","x00200052","x00200060","x00200062","x00200100","x00200105","x00200110","x00200200","x00201000","x00201002","x00201004","x00201040","x00201041","x00201070","x00201200","x00201202","x00201204","x00201206","x00201208","x00201209","x00203401","x00203404","x00203406","x00204000","x00209056","x00209057","x00209071","x00209072","x00209111","x00209113","x00209116","x00209128","x00209153","x00209156","x00209157","x00209158","x00209161","x00209162","x00209163","x00209164","x00209165","x00209167","x00209213","x00209221","x00209222","x00209228","x00209238","x00280002","x00280004","x00280006","x00280008","x00280009","x00280010","x00280011","x00280012","x00280014","x00280030","x00280031","x00280032","x00280034","x00280051","x00280100","x00280101","x00280102","x00280103","x00280106","x00280107","x00280108","x00280109","x00280110","x00280111","x00280120","x00280300","x00280301","x00281040","x00281041","x00281050","x00281051","x00281052","x00281053","x00281054","x00281055","x00281090","x00281101","x00281102","x00281103","x00281199","x00281201","x00281202","x00281203","x00281214","x00281221","x00281222","x00281223","x00281300","x00281350","x00281351","x00282110","x00282112","x00283000","x00283002","x00283003","x00283004","x00283006","x00283010","x00283110","x00284000","x00285000","x00286010","x00286020","x00286022","x00286030","x00286040","x00286100","x00286101","x00286102","x00286110","x00286112","x00286114","x00286120","x00286190","x00289001","x00289002","x00289003","x00289099","x00289108","x00289110","x00289132","x00289145","x00289235","x0032000a","x0032000c","x00320012","x00320032","x00320033","x00320034","x00320035","x00321000","x00321001","x00321010","x00321011","x00321020","x00321021","x00321030","x00321032","x00321033","x00321040","x00321041","x00321050","x00321051","x00321055","x00321060","x00321064","x00321070","x00324000","x00380004","x00380008","x00380010","x00380011","x00380016","x0038001a","x0038001b","x0038001c","x0038001d","x0038001e","x00380020","x00380021","x00380030","x00380032","x00380040","x00380044","x00380050","x00380060","x00380061","x00380062","x00380300","x00380400","x00380500","x00381234","x00384000","x003a0004","x003a0005","x003a0010","x003a001a","x003a0020","x003a0200","x003a0202","x003a0203","x003a0205","x003a0208","x003a0209","x003a020a","x003a020c","x003a0210","x003a0211","x003a0212","x003a0213","x003a0214","x003a0215","x003a0218","x003a021a","x003a0220","x003a0221","x003a0222","x003a0223","x00400001","x00400002","x00400003","x00400004","x00400005","x00400006","x00400007","x00400008","x00400009","x0040000b","x00400010","x00400011","x00400012","x00400020","x00400100","x00400220","x00400241","x00400242","x00400243","x00400244","x00400245","x00400248","x00400250","x00400251","x00400252","x00400253","x00400254","x00400255","x00400260","x00400270","x00400275","x00400280","x00400281","x00400293","x00400294","x00400295","x00400296","x00400300","x00400301","x00400302","x00400303","x00400306","x00400307","x0040030e","x00400310","x00400312","x00400314","x00400316","x00400318","x00400320","x00400321","x00400324","x00400330","x00400340","x00400400","x0040050a","x00400550","x00400551","x00400555","x00400556","x0040059a","x004006fa","x0040071a","x0040072a","x0040073a","x0040074a","x004008d8","x004008da","x004008ea","x00401001","x00401002","x00401003","x00401004","x00401005","x00401008","x00401009","x00401010","x00401011","x00401102","x00401103","x00401400","x00402001","x00402004","x00402005","x00402008","x00402009","x00402010","x00402016","x00402017","x00402400","x00403001","x00404023","x00404025","x00404027","x00404030","x00404034","x00404035","x00404036","x00404037","x00408302","x00409096","x00409210","x00409211","x00409212","x00409216","x00409224","x00409225","x0040a010","x0040a027","x0040a030","x0040a032","x0040a040","x0040a043","x0040a050","x0040a073","x0040a075","x0040a078","x0040a07a","x0040a07c","x0040a088","x0040a0b0","x0040a120","x0040a121","x0040a122","x0040a123","x0040a124","x0040a130","x0040a132","x0040a136","x0040a138","x0040a13a","x0040a160","x0040a168","x0040a180","x0040a195","x0040a300","x0040a30a","x0040a360","x0040a370","x0040a372","x0040a375","x0040a385","x0040a491","x0040a492","x0040a493","x0040a504","x0040a525","x0040a730","x0040b020","x0040db00","x0040db06","x0040db07","x0040db0b","x0040db0c","x0040db0d","x0040db73","x00603000","x00604000","x0070031a","x00880140","x00880200","x00880906","x00880910","x00880912","x04000100","x20300020","x30060024","x300600c2","x300a0013","x40000010","x40004000","x40080042","x40080102","x4008010a","x4008010b","x4008010c","x40080111","x40080114","x40080115","x40080118","x40080119","x4008011a","x40080202","x40080300","x40084000","xfffafffa","xfffcfffc"];
var names = ["SpecificCharacterSet","ImageType","InstanceCreationDate","InstanceCreationTime","InstanceCreatorUID","SOPClassUID","SOPInstanceUID","StudyDate","SeriesDate","AcquisitionDate","ContentDate","OverlayDate","CurveDate","AcquisitionDatetime","StudyTime","SeriesTime","AcquisitionTime","ContentTime","OverlayTime","CurveTime","AccessionNumber","QueryRetrieveLevel","RetrieveAET","InstanceAvailability","FailedSOPInstanceUIDList","Modality","ModalitiesInStudy","ConversionType","PresentationIntentType","Manufacturer","InstitutionName","InstitutionAddress","InstitutionCodeSeq","ReferringPhysicianName","ReferringPhysicianAddress","ReferringPhysicianPhoneNumbers","ReferringPhysiciansIDSeq","CodeValue","CodingSchemeDesignator","CodingSchemeVersion","CodeMeaning","MappingResource","ContextGroupVersion","ContextGroupLocalVersion","CodeSetExtensionFlag","PrivateCodingSchemeCreatorUID","CodeSetExtensionCreatorUID","ContextIdentifier","TimezoneOffsetFromUTC","StationName","StudyDescription","ProcedureCodeSeq","SeriesDescription","InstitutionalDepartmentName","PhysicianOfRecord","PhysicianOfRecordIdSeq","PerformingPhysicianName","PerformingPhysicianIdSeq","NameOfPhysicianReadingStudy","PhysicianReadingStudyIdSeq","OperatorName","OperatorsIdentificationSeq","AdmittingDiagnosisDescription","AdmittingDiagnosisCodeSeq","ManufacturerModelName","RefResultsSeq","RefStudySeq","RefPPSSeq","RefSeriesSeq","RefPatientSeq","RefVisitSeq","RefOverlaySeq","RefImageSeq","RefCurveSeq","RefInstanceSeq","RefSOPClassUID","RefSOPInstanceUID","SOPClassesSupported","RefFrameNumber","TransactionUID","FailureReason","FailedSOPSeq","RefSOPSeq","DerivationDescription","SourceImageSeq","StageName","StageNumber","NumberOfStages","ViewNumber","NumberOfEventTimers","NumberOfViewsInStage","EventElapsedTime","EventTimerName","StartTrim","StopTrim","RecommendedDisplayFrameRate","AnatomicRegionSeq","AnatomicRegionModifierSeq","PrimaryAnatomicStructureSeq","AnatomicStructureSpaceRegionSeq","PrimaryAnatomicStructureModifierSeq","TransducerPositionSeq","TransducerPositionModifierSeq","TransducerOrientationSeq","TransducerOrientationModifierSeq","IrradiationEventUID","IdentifyingComments","FrameType","ReferringImageEvidenceSeq","RefRawDataSeq","CreatorVersionUID","DerivationImageSeq","SourceImageEvidenceSeq","PixelPresentation","VolumetricProperties","VolumeBasedCalculationTechnique","ComplexImageComponent","AcquisitionContrast","DerivationCodeSeq","RefGrayscalePresentationStateSeq","PatientName","PatientID","IssuerOfPatientID","PatientBirthDate","PatientBirthTime","PatientSex","PatientInsurancePlanCodeSeq","PatientPrimaryLanguageCodeSeq","PatientPrimaryLanguageModifierCodeSeq","OtherPatientIDs","OtherPatientNames","OtherPatientIDsSeq","PatientBirthName","PatientAge","PatientSize","PatientWeight","PatientAddress","InsurancePlanIdentification","PatientMotherBirthName","MilitaryRank","BranchOfService","MedicalRecordLocator","MedicalAlerts","ContrastAllergies","CountryOfResidence","RegionOfResidence","PatientPhoneNumbers","EthnicGroup","Occupation","SmokingStatus","AdditionalPatientHistory","PregnancyStatus","LastMenstrualDate","PatientReligiousPreference","PatientSexNeutered","ResponsiblePerson","ResponsibleOrganization","PatientComments","ClinicalTrialSponsorName","ClinicalTrialProtocolID","ClinicalTrialProtocolName","ClinicalTrialSiteID","ClinicalTrialSiteName","ClinicalTrialSubjectID","ClinicalTrialSubjectReadingID","ClinicalTrialTimePointID","ClinicalTrialTimePointDescription","CoordinatingCenterName","PatientIdentityRemoved","DeIdentificationMethod","DeIdentificationMethodCodeSeq","BlockOwner","ProjectName","TrialName","SiteName","SiteID","ContrastBolusAgent","ContrastBolusAgentSeq","ContrastBolusAdministrationRouteSeq","BodyPartExamined","ScanningSeq","SeqVariant","ScanOptions","MRAcquisitionType","SequenceName","AngioFlag","InterventionDrugInformationSeq","InterventionDrugStopTime","InterventionDrugDose","InterventionDrugCodeSeq","AdditionalDrugSeq","Radiopharmaceutical","InterventionDrugName","InterventionDrugStartTime","InterventionalTherapySeq","TherapyType","InterventionalStatus","TherapyDescription","CineRate","SliceThickness","KVP","CountsAccumulated","AcquisitionTerminationCondition","EffectiveSeriesDuration","AcquisitionStartCondition","AcquisitionStartConditionData","AcquisitionTerminationConditionData","RepetitionTime","EchoTime","InversionTime","NumberOfAverages","ImagingFrequency","ImagedNucleus","EchoNumber","MagneticFieldStrength","SpacingBetweenSlices","NumberOfPhaseEncodingSteps","DataCollectionDiameter","EchoTrainLength","PercentSampling","PercentPhaseFieldOfView","PixelBandwidth","DeviceSerialNumber","DeviceUID","PlateID","GeneratorID","CassetteID","GantryID","SecondaryCaptureDeviceID","HardcopyCreationDeviceID","DateOfSecondaryCapture","TimeOfSecondaryCapture","SecondaryCaptureDeviceManufacturer","HardcopyDeviceManufacturer","SecondaryCaptureDeviceManufacturerModelName","SecondaryCaptureDeviceSoftwareVersion","HardcopyDeviceSoftwareVersion","HardcopyDeviceManfuacturerModelName","SoftwareVersion","VideoImageFormatAcquired","DigitalImageFormatAcquired","ProtocolName","ContrastBolusRoute","ContrastBolusVolume","ContrastBolusStartTime","ContrastBolusStopTime","ContrastBolusTotalDose","SyringeCounts","ContrastFlowRate","ContrastFlowDuration","ContrastBolusIngredient","ContrastBolusIngredientConcentration","SpatialResolution","TriggerTime","TriggerSourceOrType","NominalInterval","FrameTime","FramingType","FrameTimeVector","FrameDelay","ImageTriggerDelay","MultiplexGroupTimeOffset","TriggerTimeOffset","SynchronizationTrigger","SynchronizationChannel","TriggerSamplePosition","RadiopharmaceuticalRoute","RadiopharmaceuticalVolume","RadiopharmaceuticalStartTime","RadiopharmaceuticalStopTime","RadionuclideTotalDose","RadionuclideHalfLife","RadionuclidePositronFraction","RadiopharmaceuticalSpecificActivity","BeatRejectionFlag","LowRRValue","HighRRValue","IntervalsAcquired","IntervalsRejected","PVCRejection","SkipBeats","HeartRate","CardiacNumberOfImages","TriggerWindow","ReconstructionDiameter","DistanceSourceToDetector","DistanceSourceToPatient","EstimatedRadiographicMagnificationFactor","GantryDetectorTilt","GantryDetectorSlew","TableHeight","TableTraverse","TableMotion","TableVerticalIncrement","TableLateralIncrement","TableLongitudinalIncrement","TableAngle","TableType","RotationDirection","AngularPosition","RadialPosition","ScanArc","AngularStep","CenterOfRotationOffset","FieldOfViewShape","FieldOfViewDimension","ExposureTime","XRayTubeCurrent","Exposure","ExposureInuAs","AveragePulseWidth","RadiationSetting","RectificationType","RadiationMode","ImageAreaDoseProduct","FilterType","TypeOfFilters","IntensifierSize","ImagerPixelSpacing","Grid","GeneratorPower","CollimatorGridName","CollimatorType","FocalDistance","XFocusCenter","YFocusCenter","FocalSpot","AnodeTargetMaterial","BodyPartThickness","CompressionForce","DateOfLastCalibration","TimeOfLastCalibration","ConvolutionKernel","ActualFrameDuration","CountRate","PreferredPlaybackSequencing","ReceiveCoilName","TransmitCoilName","PlateType","PhosphorType","ScanVelocity","WholeBodyTechnique","ScanLength","AcquisitionMatrix","PhaseEncodingDirection","FlipAngle","VariableFlipAngleFlag","SAR","dBDt","AcquisitionDeviceProcessingDescription","AcquisitionDeviceProcessingCode","CassetteOrientation","CassetteSize","ExposuresOnPlate","RelativeXRayExposure","ColumnAngulation","TomoLayerHeight","TomoAngle","TomoTime","TomoType","TomoClass","NumberofTomosynthesisSourceImages","PositionerMotion","PositionerType","PositionerPrimaryAngle","PositionerSecondaryAngle","PositionerPrimaryAngleIncrement","PositionerSecondaryAngleIncrement","DetectorPrimaryAngle","DetectorSecondaryAngle","ShutterShape","ShutterLeftVerticalEdge","ShutterRightVerticalEdge","ShutterUpperHorizontalEdge","ShutterLowerHorizontalEdge","CenterOfCircularShutter","RadiusOfCircularShutter","VerticesOfPolygonalShutter","ShutterPresentationValue","ShutterOverlayGroup","CollimatorShape","CollimatorLeftVerticalEdge","CollimatorRightVerticalEdge","CollimatorUpperHorizontalEdge","CollimatorLowerHorizontalEdge","CenterOfCircularCollimator","RadiusOfCircularCollimator","VerticesOfPolygonalCollimator","AcquisitionTimeSynchronized","TimeSource","TimeDistributionProtocol","AcquisitionComments","OutputPower","TransducerData","FocusDepth","ProcessingFunction","PostprocessingFunction","MechanicalIndex","ThermalIndex","CranialThermalIndex","SoftTissueThermalIndex","SoftTissueFocusThermalIndex","SoftTissueSurfaceThermalIndex","DepthOfScanField","PatientPosition","ViewPosition","ProjectionEponymousNameCodeSeq","ImageTransformationMatrix","ImageTranslationVector","Sensitivity","SeqOfUltrasoundRegions","RegionSpatialFormat","RegionDataType","RegionFlags","RegionLocationMinX0","RegionLocationMinY0","RegionLocationMaxX1","RegionLocationMaxY1","ReferencePixelX0","ReferencePixelY0","PhysicalUnitsXDirection","PhysicalUnitsYDirection","ReferencePixelPhysicalValueX","ReferencePixelPhysicalValueY","PhysicalDeltaX","PhysicalDeltaY","TransducerFrequency","TransducerType","PulseRepetitionFrequency","DopplerCorrectionAngle","SteeringAngle","DopplerSampleVolumeXPosition","DopplerSampleVolumeYPosition","TMLinePositionX0","TMLinePositionY0","TMLinePositionX1","TMLinePositionY1","PixelComponentOrganization","PixelComponentMask","PixelComponentRangeStart","PixelComponentRangeStop","PixelComponentPhysicalUnits","PixelComponentDataType","NumberOfTableBreakPoints","TableOfXBreakPoints","TableOfYBreakPoints","NumberOfTableEntries","TableOfPixelValues","TableOfParameterValues","DetectorConditionsNominalFlag","DetectorTemperature","DetectorType","DetectorConfiguration","DetectorDescription","DetectorMode","DetectorID","DateOfLastDetectorCalibration","TimeOfLastDetectorCalibration","ExposuresOnDetectorSinceLastCalibration","ExposuresOnDetectorSinceManufactured","DetectorTimeSinceLastExposure","DetectorActiveTime","DetectorActivationOffsetFromExposure","DetectorBinning","DetectorElementPhysicalSize","DetectorElementSpacing","DetectorActiveShape","DetectorActiveDimension","DetectorActiveOrigin","FieldOfViewOrigin","FieldOfViewRotation","FieldOfViewHorizontalFlip","GridAbsorbingMaterial","GridSpacingMaterial","GridThickness","GridPitch","GridAspectRatio","GridPeriod","GridFocalDistance","FilterMaterial","FilterThicknessMinimum","FilterThicknessMaximum","ExposureControlMode","ExposureControlModeDescription","ExposureStatus","PhototimerSetting","ExposureTimeInuS","XRayTubeCurrentInuA","ContentQualification","PulseSequenceName","MRImagingModifierSeq","EchoPulseSeq","InversionRecovery","FlowCompensation","MultipleSpinEcho","MultiPlanarExcitation","PhaseContrast","TimeOfFlightContrast","Spoiling","SteadyStatePulseSeq","EchoPlanarPulseSeq","TagAngleFirstAxis","MagnetizationTransfer","T2Preparation","BloodSignalNulling","SaturationRecovery","SpectrallySelectedSuppression","SpectrallySelectedExcitation","SpatialPreSaturation","Tagging","OversamplingPhase","TagSpacingFirstDimension","GeometryOfKSpaceTraversal","SegmentedKSpaceTraversal","RectilinearPhaseEncodeReordering","TagThickness","PartialFourierDirection","GatingSynchronizationTechnique","ReceiveCoilManufacturerName","MRReceiveCoilSeq","ReceiveCoilType","QuadratureReceiveCoil","MultiCoilDefinitionSeq","MultiCoilConfiguration","MultiCoilElementName","MultiCoilElementUsed","MRTransmitCoilSeq","TransmitCoilManufacturerName","TransmitCoilType","SpectralWidth","ChemicalShiftReference","VolumeLocalizationTechnique","MRAcquisitionFrequencyEncodingSteps","DeCoupling","DeCoupledNucleus","DeCouplingFrequency","DeCouplingMethod","DeCouplingChemicalShiftReference","KSpaceFiltering","TimeDomainFiltering","NumberOfZeroFills","BaselineCorrection","CardiacRRIntervalSpecified","AcquisitionDuration","FrameAcquisitionDatetime","DiffusionDirectionality","DiffusionGradientDirectionSeq","ParallelAcquisition","ParallelAcquisitionTechnique","InversionTimes","MetaboliteMapDescription","PartialFourier","EffectiveEchoTime","ChemicalShiftSeq","CardiacSignalSource","DiffusionBValue","DiffusionGradientOrientation","VelocityEncodingDirection","VelocityEncodingMinimumValue","NumberOfKSpaceTrajectories","CoverageOfKSpace","SpectroscopyAcquisitionPhaseRows","ParallelReductionFactorInPlane","TransmitterFrequency","ResonantNucleus","FrequencyCorrection","MRSpectroscopyFOVGeometrySeq","SlabThickness","SlabOrientation","MidSlabPosition","MRSpatialSaturationSeq","MRTimingAndRelatedParametersSeq","MREchoSeq","MRModifierSeq","MRDiffusionSeq","CardiacTriggerSeq","MRAveragesSeq","MRFOVGeometrySeq","VolumeLocalizationSeq","SpectroscopyAcquisitionDataColumns","DiffusionAnisotropyType","FrameReferenceDatetime","MetaboliteMapSeq","ParallelReductionFactorOutOfPlane","SpectroscopyAcquisitionOutOfPlanePhaseSteps","BulkMotionStatus","ParallelReductionFactorSecondInPlane","CardiacBeatRejectionTechnique","RespiratoryMotionCompensation","RespiratorySignalSource","BulkMotionCompensationTechnique","BulkMotionSignal","ApplicableSafetyStandardAgency","ApplicableSafetyStandardVersion","OperationModeSeq","OperatingModeType","OperationMode","SpecificAbsorptionRateDefinition","GradientOutputType","SpecificAbsorptionRateValue","GradientOutput","FlowCompensationDirection","TaggingDelay","ChemicalShiftsMinimumIntegrationLimit","ChemicalShiftsMaximumIntegrationLimit","MRVelocityEncodingSeq","FirstOrderPhaseCorrection","WaterReferencedPhaseCorrection","MRSpectroscopyAcquisitionType","RespiratoryMotionStatus","VelocityEncodingMaximumValue","TagSpacingSecondDimension","TagAngleSecondAxis","FrameAcquisitionDuration","MRImageFrameTypeSeq","MRSpectroscopyFrameTypeSeq","MRAcquisitionPhaseEncodingStepsInPlane","MRAcquisitionPhaseEncodingStepsOutOfPlane","SpectroscopyAcquisitionPhaseColumns","CardiacMotionStatus","SpecificAbsorptionRateSeq","AcquisitionProtocolDescription","ContributionDescription","StudyInstanceUID","SeriesInstanceUID","StudyID","SeriesNumber","AcquisitionNumber","InstanceNumber","ItemNumber","PatientOrientation","OverlayNumber","CurveNumber","LUTNumber","ImagePosition","ImageOrientation","FrameOfReferenceUID","Laterality","ImageLaterality","TemporalPositionIdentifier","NumberOfTemporalPositions","TemporalResolution","SynchronizationFrameOfReferenceUID","SeriesInStudy","ImagesInAcquisition","AcquisitionsInStudy","PositionReferenceIndicator","SliceLocation","OtherStudyNumbers","NumberOfPatientRelatedStudies","NumberOfPatientRelatedSeries","NumberOfPatientRelatedInstances","NumberOfStudyRelatedSeries","NumberOfStudyRelatedInstances","NumberOfSeriesRelatedInstances","ModifyingDeviceID","ModifyingDeviceManufacturer","ModifiedImageDescription","ImageComments","StackID","InStackPositionNumber","FrameAnatomySeq","FrameLaterality","FrameContentSeq","PlanePositionSeq","PlaneOrientationSeq","TemporalPositionIndex","TriggerDelayTime","FrameAcquisitionNumber","DimensionIndexValues","FrameComments","ConcatenationUID","InConcatenationNumber","InConcatenationTotalNumber","DimensionOrganizationUID","DimensionIndexPointer","FunctionalGroupSequencePointer","DimensionIndexPrivateCreator","DimensionOrganizationSeq","DimensionSeq","ConcatenationFrameOffsetNumber","FunctionalGroupPrivateCreator","SamplesPerPixel","PhotometricInterpretation","PlanarConfiguration","NumberOfFrames","FrameIncrementPointer","Rows","Columns","Planes","UltrasoundColorDataPresent","PixelSpacing","ZoomFactor","ZoomCenter","PixelAspectRatio","CorrectedImage","BitsAllocated","BitsStored","HighBit","PixelRepresentation","SmallestImagePixelValue","LargestImagePixelValue","SmallestPixelValueInSeries","LargestPixelValueInSeries","SmallestImagePixelValueInPlane","LargestImagePixelValueInPlane","PixelPaddingValue","QualityControlImage","BurnedInAnnotation","PixelIntensityRelationship","PixelIntensityRelationshipSign","WindowCenter","WindowWidth","RescaleIntercept","RescaleSlope","RescaleType","WindowCenterWidthExplanation","RecommendedViewingMode","RedPaletteColorLUTDescriptor","GreenPaletteColorLUTDescriptor","BluePaletteColorLUTDescriptor","PaletteColorLUTUID","RedPaletteColorLUTData","GreenPaletteColorLUTData","BluePaletteColorLUTData","LargePaletteColorLUTUid","SegmentedRedPaletteColorLUTData","SegmentedGreenPaletteColorLUTData","SegmentedBluePaletteColorLUTData","ImplantPresent","PartialView","PartialViewDescription","LossyImageCompression","LossyImageCompressionRatio","ModalityLUTSeq","LUTDescriptor","LUTExplanation","ModalityLUTType","LUTData","VOILUTSeq","SoftcopyVOILUTSeq","ImagePresentationComments","BiPlaneAcquisitionSeq","RepresentativeFrameNumber","FrameNumbersOfInterest","FrameOfInterestDescription","MaskPointer","RWavePointer","MaskSubtractionSeq","MaskOperation","ApplicableFrameRange","MaskFrameNumbers","ContrastFrameAveraging","MaskSubPixelShift","TIDOffset","MaskOperationExplanation","DataPointRows","DataPointColumns","SignalDomain","LargestMonochromePixelValue","DataRepresentation","PixelMatrixSeq","FrameVOILUTSeq","PixelValueTransformationSeq","SignalDomainRows","StudyStatusID","StudyPriorityID","StudyIDIssuer","StudyVerifiedDate","StudyVerifiedTime","StudyReadDate","StudyReadTime","ScheduledStudyStartDate","ScheduledStudyStartTime","ScheduledStudyStopDate","ScheduledStudyStopTime","ScheduledStudyLocation","ScheduledStudyLocationAET","ReasonforStudy","RequestingPhysician","RequestingService","StudyArrivalDate","StudyArrivalTime","StudyCompletionDate","StudyCompletionTime","StudyComponentStatusID","RequestedProcedureDescription","RequestedProcedureCodeSeq","RequestedContrastAgent","StudyComments","RefPatientAliasSeq","VisitStatusID","AdmissionID","IssuerOfAdmissionID","RouteOfAdmissions","ScheduledAdmissionDate","ScheduledAdmissionTime","ScheduledDischargeDate","ScheduledDischargeTime","ScheduledPatientInstitutionResidence","AdmittingDate","AdmittingTime","DischargeDate","DischargeTime","DischargeDiagnosisDescription","DischargeDiagnosisCodeSeq","SpecialNeeds","ServiceEpisodeID","IssuerOfServiceEpisodeId","ServiceEpisodeDescription","CurrentPatientLocation","PatientInstitutionResidence","PatientState","ReferencedPatientAliasSeq","VisitComments","WaveformOriginality","NumberOfWaveformChannels","NumberOfWaveformSamples","SamplingFrequency","MultiplexGroupLabel","ChannelDefinitionSeq","WaveformChannelNumber","ChannelLabel","ChannelStatus","ChannelSourceSeq","ChannelSourceModifiersSeq","SourceWaveformSeq","ChannelDerivationDescription","ChannelSensitivity","ChannelSensitivityUnitsSeq","ChannelSensitivityCorrectionFactor","ChannelBaseline","ChannelTimeSkew","ChannelSampleSkew","ChannelOffset","WaveformBitsStored","FilterLowFrequency","FilterHighFrequency","NotchFilterFrequency","NotchFilterBandwidth","ScheduledStationAET","SPSStartDate","SPSStartTime","SPSEndDate","SPSEndTime","ScheduledPerformingPhysicianName","SPSDescription","ScheduledProtocolCodeSeq","SPSID","","ScheduledStationName","SPSLocation","PreMedication","SPSStatus","SPSSeq","RefNonImageCompositeSOPInstanceSeq","PerformedStationAET","PerformedStationName","PerformedLocation","PPSStartDate","PPSStartTime","PerformedStationNameCodeSeq","PPSEndDate","PPSEndTime","PPSStatus","PPSID","PPSDescription","PerformedProcedureTypeDescription","PerformedProtocolCodeSeq","ScheduledStepAttributesSeq","RequestAttributesSeq","PPSComments","PPSDiscontinuationReasonCodeSeq","QuantitySeq","Quantity","MeasuringUnitsSeq","BillingItemSeq","TotalTimeOfFluoroscopy","TotalNumberOfExposures","EntranceDose","ExposedArea","DistanceSourceToEntrance","DistanceSourceToSupport","ExposureDoseSeq","CommentsOnRadiationDose","XRayOutput","HalfValueLayer","OrganDose","OrganExposed","BillingProcedureStepSeq","FilmConsumptionSeq","BillingSuppliesAndDevicesSeq","RefProcedureStepSeq","PerformedSeriesSeq","SPSComments","SpecimenAccessionNumber","SpecimenSeq","SpecimenIdentifier","AcquisitionContextSeq","AcquisitionContextDescription","SpecimenTypeCodeSeq","SlideIdentifier","ImageCenterPointCoordinatesSeq","XOffsetInSlideCoordinateSystem","YOffsetInSlideCoordinateSystem","ZOffsetInSlideCoordinateSystem","PixelSpacingSeq","CoordinateSystemAxisCodeSeq","MeasurementUnitsCodeSeq","RequestedProcedureID","ReasonForTheRequestedProcedure","RequestedProcedurePriority","PatientTransportArrangements","RequestedProcedureLocation","ConfidentialityCode","ReportingPriority","NamesOfIntendedRecipientsOfResults","IntendedRecipientsOfResultsIDSequence","PersonAddress","PersonTelephoneNumbers","RequestedProcedureComments","ReasonForTheImagingServiceRequest","IssueDateOfImagingServiceRequest","IssueTimeOfImagingServiceRequest","OrderEnteredBy","OrderEntererLocation","OrderCallbackPhoneNumber","PlacerOrderNumber","FillerOrderNumber","ImagingServiceRequestComments","ConfidentialityPatientData","RefGenPurposeSchedProcStepTransUID","ScheduledStationNameCodeSeq","ScheduledStationGeographicLocCodeSeq","PerformedStationGeoLocCodeSeq","ScheduledHumanPerformersSeq","ActualHumanPerformersSequence","HumanPerformersOrganization","HumanPerformersName","EntranceDoseInmGy","RealWorldValueMappingSeq","LUTLabel","RealWorldValueLUTLastValueMappedUS","RealWorldValueLUTData","RealWorldValueLUTFirstValueMappedUS","RealWorldValueIntercept","RealWorldValueSlope","RelationshipType","VerifyingOrganization","VerificationDateTime","ObservationDateTime","ValueType","ConceptNameCodeSeq","ContinuityOfContent","VerifyingObserverSeq","VerifyingObserverName","AuthorObserverSequence","ParticipantSequence","CustodialOrganizationSeq","VerifyingObserverIdentificationCodeSeq","RefWaveformChannels","DateTime","Date","Time","PersonName","UID","TemporalRangeType","RefSamplePositions","RefFrameNumbers","RefTimeOffsets","RefDatetime","TextValue","ConceptCodeSeq","AnnotationGroupNumber","ModifierCodeSeq","MeasuredValueSeq","NumericValue","PredecessorDocumentsSeq","RefRequestSeq","PerformedProcedureCodeSeq","CurrentRequestedProcedureEvidenceSeq","PertinentOtherEvidenceSeq","CompletionFlag","CompletionFlagDescription","VerificationFlag","ContentTemplateSeq","IdenticalDocumentsSeq","ContentSeq","AnnotationSeq","TemplateIdentifier","TemplateVersion","TemplateLocalVersion","TemplateExtensionFlag","TemplateExtensionOrganizationUID","TemplateExtensionCreatorUID","RefContentItemIdentifier","OverlayData","OverlayComments","FiducialUID","StorageMediaFilesetUID","IconImageSequence","TopicSubject","TopicAuthor","TopicKeyWords","DigitalSignatureUID","TextString","ReferencedFrameOfReferenceUID","RelatedFrameOfReferenceUID","DoseReferenceUID","Arbitrary","TextComments","ResultsIDIssuer","InterpretationRecorder","InterpretationTranscriber","InterpretationText","InterpretationAuthor","InterpretationApproverSequence","PhysicianApprovingInterpretation","InterpretationDiagnosisDescription","ResultsDistributionListSeq","DistributionName","DistributionAddress","InterpretationIdIssuer","Impressions","ResultComments","DigitalSignaturesSeq","DataSetTrailingPadding"];

/**
 * Read DICOM value from bytearray for element
 */
function getDicomValue(dataSet, element) {
	var res = null;
	if (element !== undefined) {
		var str = dataSet.string(element.tag);
		if (str !== undefined) {
			res = str;
		}
	}
	return res;
}

/**
 * Set value as char in byte array for element
 */
function setDicomValue(dataSet, element, value) {
	for(var i=0; i < element.length; i++) {
		var char = (value.length > i) ? value.charCodeAt(i) : 32;
		dataSet.byteArray[element.dataOffset + i] = char;
	}
}

function append(value, profileName, date) {
	return profileName + "-" + date;
}

function empty() {
	return " ";
}

function hash(string, maxlen) {
	if (string == null) string = "null";
	if (maxlen < 1) maxlen = 10;

	var hash = 0;
	if (maxlen == 0) return hash;
	for (var i = 0; i < maxlen; i++) {
		var char = string.charCodeAt(i);
		hash = ((hash << 5) - hash) + char;
		hash = hash & hash; // Convert to 32bit integer
	}

	return hash;
}

function hashname(string, length, wordCount) {
	if (wordCount <= 0){
		wordCount = 10; //Integer.MAX_VALUE
	}
	var words = string.split("\\^");
	string = "";
	for (var i = 0; i < words.length && i < wordCount; i++) {
		string += words[i];
	}
	string = string.replace("[\\s,'\\^\\.]","").toUpperCase();
	return hash(string, length);
}

function hashPtID(siteid, ptid, maxlen){
	if (siteid == null){
		siteid = "";
	} else {
		siteid = siteid.trim();
	}
	if (ptid == null) {
		ptid = "null";
	} else {
		ptid = ptid.trim();
	}
	return hash("[" + siteid + "]" + ptid, maxlen);
}

function hashUID(prefix, uid){
	prefix = prefix.trim();
	if (!(prefix === "") && !(prefix == ".")){
		prefix += ".";
	}

	//Create the replacement UID
	var hashString = MD5.md5(uid); //DigestUtil.getUSMD5(uid);
	var extra = hashString.startsWith("0") ? "9" : "";
	var newuid = prefix + extra + hashString;
	if (newuid.length > 64) newuid = newuid.substring(0,64);
	return newuid;
}

/**
 * Empty time
 * @returns {string}
 */
function emptyTime(){
	return "0";
}

/**
 * Days to increment
 * @param dateStr
 * @param increment
 */
function incrementDate(dateStr, increment){
	var inc = Number(increment) * 24 * 3600 * 1000;
	var date = new Date();
	date.setFullYear(Number(dateStr.substring(0, 4)));
	date.setUTCMonth(Number(dateStr.substring(4, 6)) - 1);
	date.setUTCDate(Number(dateStr.substring(6, 8)));
	date.setTime(date.getTime() + inc);
	return formatDate(date);
}

function pad(num, size) {
	var s = num+"";
	while (s.length < size) s = "0" + s;
	return s;
}


/**
 * Reset date to now
 * @returns {string}
 */
function resetDate(){
	return formatDate(new Date());
}

function formatDate(date){
	return "" + date.getUTCFullYear() + (date.getUTCMonth() + 1) + date.getUTCDate();
}

function resetVrDate(){
	return formatDate(new Date());
}

function resetTime(){
	return new Date().getTime().toString();
}

/**
 * Reset time to now
 * @returns {string}
 */
function resetVrTime(length){
	var res = formatTime(new Date(), length);
	if (res.length > length) {
		res = res.substring(0, length);
	}
	return res;
}

/**
 * Default YYYYMMDDHHMMSS.FFFFFF(+/-)ZZZZ (YYYY = Year, MM = Month, DD = Day, HH = Hour, MM = Minute, SS = Second, FFFFFF = Fractional Second, & = "+" or "-", and ZZZZ = Hours and Minutes of offset)
 * FOR 10 length HHMMSS.FFF
 * FOR 12 length HHMMSS.FFFFF
 */
function formatTime(date, length){
	switch (length){
		case 10:
			return "" + pad(date.getHours(), 2) + pad(date.getMinutes(), 2) + pad(date.getSeconds(), 2) + "." + pad(date.getMilliseconds(), 3);
		case 12:
			return "" + pad(date.getHours(), 2) + pad(date.getMinutes(), 2) + pad(date.getSeconds(), 2) + "." + pad(date.getMilliseconds(), 3) + "00";
		default:
			return "" + formatDate(date) + pad(date.getHours(), 2) + pad(date.getMinutes(), 2) + pad(date.getSeconds(), 2) + "." + pad(date.getMilliseconds(), 3) + "000+0001";
	}
}

/**
 * Reset vr Age string
 */
function resetVrAgeString(){
	return "018M";
}

/**
 * Reset vr tag
 */
function resetVrAttrTag(){
	return "00080005";
}



// from http://stackoverflow.com/questions/1349404/generate-a-string-of-5-random-characters-in-javascript
/**
 * Generate random string using template
 */
function rdmStrTemplate(length, possible) {
	var text = "";
	for( var i=0; i < length; i++ )
		text += possible.charAt(Math.floor(Math.random() * possible.length));

	return text;
}

/**
 * Generate random string for length
 */
function rdmStr(length) {
	return rdmStrTemplate(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
}

/**
 * Generate random number
 */
function rdmNumber(length) {
	return rdmStrTemplate(length, "0123456789");
}

/**
 * Generate random signed number for vr types IS, SL and SS
 */
function rdmSgnNumber(length) {
	return "0" + rdmNumber(length - 1);
}

/**
 * Generate random float for vr types FL, FD and OF
 */
function rdmFloat(length) {
	if (length > 2) {
		return "00" + rdmNumber(length - 2);
	} else {
		return rdmNumber(length);
	}

}

/**
 * Generate random UID for vr type UI
 */
function rdmUID(currVal, length) {
	var uid = "";
	if (currVal.includes(".")) {
		var vals = currVal.split(".");

		for(var i = 0; i < vals.length; i++){
			if (i != 0) {
				uid += "."
			}
			uid += rdmNumber(vals[i].length);
		}
	} else {
		uid = rdmNumber(length);
	}

	return uid;
}

/**
 * Generate random values based on element vr type
 */
function rdmValue(currVal, length, vr) {
	var val = null;

	//            console.log("vr", vr, "length", length);
	switch (vr) {
		case 'AE': // Application Entity. STRING (16 max). Excluding character code 5CH (the BACKSLASH "\" in ISO-IR 6), and control characters LF, FF, CR and ESC. (ex "hola")
			val = rdmStr(length);
			break;
		case 'AS': // Age String. STRING (4). "0"-"9" (3 times), "D", "W", "M", "Y" of Default Character Repertoire (ex. "018M")
			val = resetVrAgeString();
			break;
		case 'AT': // Attribute Tag. ULONG (4). A Data Element Tag of (0018,00FF) 4 bytes in a Little-Endian Transfer Syntax as 18H,00H,FFH,00H and in a Big-Endian Transfer Syntax as 00H,18H,00H,FFH.
			val = resetVrAttrTag();
			break;
		case 'CS': // Code String. STRING (16 max). Uppercase characters, "0"-"9", the SPACE character, and underscore "_", of the Default Character Repertoire
			val = rdmStrTemplate(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_");
			break;
		case 'DA': // Date. STRING (8). "0"-"9" of Default Character Repertoire
			val = resetVrDate();
			break;
		case 'DS': // Decimal String. STRING (16 max). "0" - "9", "+", "-", "E", "e", "." of Default Character Repertoire (ex. 0.0000001e-49)
//                    val = resetVrDecStr(length);
			val = rdmNumber(length);
			break;
		case 'DT': // Date Time. STRING (26 max). "0" - "9", "+", "-", "." of Default Character Repertoire. YYYYMMDDHHMMSS.FFFFFF(+/-)ZZZZ (YYYY = Year, MM = Month, DD = Day, HH = Hour, MM = Minute, SS = Second, FFFFFF = Fractional Second, & = "+" or "-", and ZZZZ = Hours and Minutes of offset)
			val = resetVrTime(length);
			break;
		case 'FL': // Floating Point Single. FLOAT (4). IEEE 754:1985 32-bit Floating Point Number Format
			val = rdmFloat(length);
			break;
		case 'FD': // Floating Point Double. FLOAT (8).
			val = rdmFloat(length);
			break;
		case 'IS': // Integer String. STRING (12 max). -2^31 <= n <= (2^31 - 1).
			val = rdmSgnNumber(length);
			break;
		case 'LO': // Long String. STRING (64 max).
			val = rdmStr(length);
			break;
		case 'LT': // Long Text. STRING (10240 chars max).
			val = rdmStr(length);
			break;
		case 'OB': // Other Byte String. BYTE.
			val = rdmStr(length);
			break;
		case 'OD': // Other Byte String Long. BYTE (2^32 - 8 bytes max)
			val = rdmStr(length);
			break;
		case 'OF': // Other Float String. FLOAT (2^32-4 max) A string of 32-bit IEEE 754:1985 floating point words
			val = rdmFloat(length);
			break;
		case 'OW': // Other Word String. INT.
			val = rdmStr(length);
			break;
		case 'PN': // Person Name. STRING (64 chars max). Default Character Repertoire and/or as defined by (0008,0005) excluding Control Characters LF, FF, and CR but allowing Control Character ESC.
			val = rdmStr(length);
			break;
		case 'SH': // Short String. STRING (16 chars).
			val = rdmStr(length);
			break;
		case 'SL': // Signed Long. LONG (4). - 2^31 <= n <= (2^31 - 1)
			val = rdmSgnNumber(length);
			break;
		case 'SQ': // Sequence of Items. LONG.
			//No se puede modificar la secuencia de items
			break;
		case 'SS': // Signed Short. INT (2). -2^15 <= n <= (2^15 - 1)
			val = rdmSgnNumber(length);
			break;
		case 'ST': // Short Text. STRING (1024 chars max).
			val = rdmStr(length);
			break;
		case 'TM': // Time. STRING (16 max). "0" - "9", "." of Default Character Repertoire (ex "070907.0705" represents a time of 7 hours, 9 minutes and 7.0705 seconds)
			val = resetVrTime(length);
			break;
		case 'UI': // Unique Identifier. STRING (64 chars max) "0" - "9", "." of Default Character Repertoire
			val = rdmUID(currVal, length);
			break;
		case 'UL': // Unsigned Long. ULONG (4). 0 <= n < 2^32
			val = rdmNumber(length);
			break;
		case 'UN': // Undefined. BYTE.
			val = rdmStr(length);
			break;
		case 'US': // Unsigned Short. UINT (2). 0 <= n < 2^16
			val = rdmNumber(length);
			break;
		case 'UT': // Unlimited Text. STRING (2^32-2). Limited only by the size of the maximum unsigned integer representable in a 32 bit VL field minus one, since FFFFFFFFH is reserved.
			val = rdmStr(length);
			break;
		default :
			console.error("ERROR undefined vr: ", vr);
	}
	if (val != null && val.length !== length) {
		console.error("ERROR length does not match: " + length + " != " + val.length + " for vr: " + vr);
	}
	return val;
}

/**
 * Anonymize script based on rsna ctp project
 * @param currVal
 * @param data
 * @param methodId
 */
function rsnaCtpStdstagesAnonymizer(data, element, currVal, methodId){
	var val = null;
	//            console.log(element, methodId);
	// Dependiendo del metodo modificamos el valor
	switch(Number(methodId)) {
		case 0: // Nada
			break;
		case 1: // @append(){CTP: @param(@PROFILENAME): @date():@time()}
			val = PROFILENAME + resetDate() + resetTime();
			break;
		case 2: // @empty()
			val = empty();
			break;
		case 3: // @hash(this,16)()
			val = hash(currVal, 16);
			break;
		case 4: // @hashname(this,6,2)
			val = hashname(currVal, 6, 2);
			break;
		case 5: // @hashptid(@SITEID,PatientID)
			var patIDElem = data.elements["x00100020"];
			var patientID = getDicomValue(data, patIDElem);
			val = hashPtID(SITEID, patientID, 10);
			break;
		case 6: // @hashuid(@UIDROOT,this)
			// length
			val = hashUID(UIDROOT, currVal);
			break;
		case 7: // @if(this,isblank){@remove()}{Removed by CTP} no podemos borrar
			console.error("ERROR remove is not possible");
			break;
		case 8: // @incrementdate(this,@DATEINC)
			if (currVal !== null && currVal.length > 0) {
				val = incrementDate(currVal, DATEINC);
			} else {
				val = incrementDate(resetDate(), DATEINC);
			}
			break;
		case 9: // @keep()
			break;
		case 10: // @param(@PROJECTNAME)
			val = PROJECTNAME;
			break;
		case 11: // @param(@SHORTNAME)-@hashptid(@SITEID,this,10)
			val = SHORTNAME-hashPtID(SITEID, val, 10);
			break;
		case 12: // @param(@SITEID)
			val = SITEID;
			break;
		case 13: // @param(@SITENAME)
			val = SITENAME;
			break;
		case 14: // @param(@TRIALNAME)
			val = TRIALNAME;
			break;
		case 15: // @param(@TRIALSPONSOR)
			val = TRIALSPONSOR;
			break;
		case 16: // @remove() - no podemos borrar
			console.error("ERROR remove is not possible");
			break;
		case 17: // @require()
			if (currVal != null && currVal.length > 0) {
				val = currVal;
			} else {
				val = rdmValue(currVal, currVal.length, element.vr);
			}
			break;
		case 18: // CTP
			val = "CTP";
			break;
		case 19: // CTP
			val = "YES";
			break;
		case 20: // En blanco
			val = " ";
			break;
		case 21: // Random
			val = rdmValue(currVal, currVal.length, element.vr);
			break;
		default:
			console.error("ERROR undefined method id: " + methodId);
	}
	return val;
}

/**
 * Read value for element and execute anonimize
 */
function anonymizeData(data, anonymData, action){
	var tag = "x" + action.id;
	var element = data.elements[tag];
	var currVal = getDicomValue(data, element);
	if (currVal == null) {
		//                console.log("Valor nulo para " + action.id);
	} else {
		//                console.log(action.methodId);
		var anonymVal = rsnaCtpStdstagesAnonymizer(data, element, currVal, action.methodId);
		if (anonymVal == null) {
			//                        console.log("Valor anonimizado nulo para: " + action.id + " con valor: " + currVal);
		} else {
			//                        console.log("element", element, "currVal", currVal, "anonymVal", anonymVal);
			setDicomValue(anonymData, element, anonymVal);
		}
	}
}

/**
 * Read json config and execute anonimize for each element
 */
function anonymize(data, anonymData, json){
	for(var i = 0; i < json.length; i++){
		var action = json[i];
		//                    console.log(action);
		anonymizeData(data, anonymData, action);
	}
}

/**
 * Describe file reading for anonymize and downloading a single file
 */
function anonymizeFile(file, json, zip) {
//            console.log("file", file);
	var reader = new FileReader();
	reader.onload = function(f) {
		//console.log("json",json);

		var arrayBuffer = reader.result;
		// Here we have the file data as an ArrayBuffer.  dicomParser requires as input a
		// Uint8Array so we create that here
		var byteArray = new Uint8Array(arrayBuffer);
		var data = dicomParser.parseDicom(byteArray);
//                //console.log("data", data);

		var anonymArray = byteArray.slice(0);
		var anonymData = dicomParser.parseDicom(anonymArray);
		anonymize(data, anonymData, json);
//                //console.log("anonymData", anonymData);

		// Add content to zip
		var filename = endDownload();
		var blob = new Blob([anonymData.byteArray], {type: "application/dicom"});
		zip.file(filename, blob);

		downloadZip(zip);
	};
	reader.readAsArrayBuffer(file);
}

// Download parameters
var countAddFiles;
var countEndFiles;
var totalFiles;
var filenames = [];

// Download functions
/**
 * Init download parameters
 */
function initDownload(total){
	totalFiles = total;
	countAddFiles = 0;
	countEndFiles = 0;
}

/**
 * Count a new file is going to be included into zip
 */
function addDownload(filename){
	countAddFiles++;
	filenames.push(filename);
}

/**
 * Files is ready for including into zip
 */
function endDownload(){
	countEndFiles++;
	return filenames.pop();
}

/**
 * Check if all files has been included
 */
function isDownloadReady(){
	return (countAddFiles === countEndFiles) && (countEndFiles === totalFiles);
}

/**
 * Generate zip content for downloading when all files has been included
 */
function downloadZip(zip){
	if (isDownloadReady()) {
		// Generamos el contenido del zip para descargar
		zip.generateAsync({type:"blob"})
			.then(function (content) {
				// see FileSaver.js
				saveAs(content, "dicom.zip");
			});
	}
}

//        var dataSet;

/**
 * Anonymize a bounch of files using json data configuration
 */
function anonymizeFiles(files, json){
	var zip = new JSZip();
	initDownload(files.length);

	// Anonimizamos y agregamos al zip
	for (var i = 0, f; f = files[i]; i++) {
		addDownload(f.name);
		anonymizeFile(f, json, zip);
	}
}

///**
// * User drops files
// */
//function handleFileSelect(evt) {
//	evt.stopPropagation();
//	evt.preventDefault();
//
//	// Get the FileList object that contains the list of files that were dropped
//	var files = evt.dataTransfer.files;
//
//	// Inicializamos json para la anonimizacion y la ejecutamos al recibirlo
//	var jqxhr = $.getJSON(jsonUrl, function() {
//		//console.log( "conn success" );
//	}).done(function(json) {
//		anonymizeFiles(files, json);
//	}).fail(function() {
//		//console.log( "error" );
//	});
//}
//
///**
// * User moves files over drag and drop container
// * @param evt
// */
//function handleDragOver(evt) {
//	evt.stopPropagation();
//	evt.preventDefault();
//	evt.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
//}
//
//// Setup the dnd listeners.
//var dropZone = document.getElementById('dropZone');
//dropZone.addEventListener('dragover', handleDragOver, false);
//dropZone.addEventListener('drop', handleFileSelect, false);