#ifndef SERVANT_MANAGEMENT
#define SERVANT_MANAGEMENT

module ServantManagement
{
    enum CalculationType { sum, sumdis};

    // An object designed to preserve extensive data sets and facilitate (expensive) computations utilizing the stored information.
    interface IBigDataObject {
        int calculateOnBigData(CalculationType calculationType);
    };

    // State-retaining entity, easily instantiated, and offering access to its current state.
    interface ISimpleObject {
        long getBornTime();
    };
};

#endif
