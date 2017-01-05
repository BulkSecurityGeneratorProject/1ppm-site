(function() {
    'use strict';

    angular
        .module('1PpmApp')
        .controller('MemberDetailController', MemberDetailController);

    MemberDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Member', 'Project'];

    function MemberDetailController($scope, $rootScope, $stateParams, previousState, entity, Member, Project) {
        var vm = this;

        vm.member = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('1PpmApp:memberUpdate', function(event, result) {
            vm.member = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
