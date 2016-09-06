/*! Copyright (c) 2016, Salesforce.com, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *      Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 *      Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 *      Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or promote products derived from this software
 *      without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
/**
 * Created by liuxizi.xu on 9/2/16.
 */
'use strict';

angular.module('argus.directives')
    .directive('tableList', function() {
        return {
            restrict: "E",
            templateUrl: 'js/templates/tableList.html',
            scope: {
                properties: '=',
                colName: '=',
                dataSet: '=data',
                addItem: '&',
                delete: '&',
                disabled: '&',
                enable: '&'
            },
            controller: ['$scope', 'Storage', function($scope, Storage) {
                // TODO: move this to a service
                // itemsPerPage setting
                $scope.itemsPerPageOptions = [5, 10, 15, 25, 50, 100, 200];
                var itemsPerPageFromStorage = $scope.properties.type + '-itemsPerPage';
                $scope.itemsPerPage = Storage.get(itemsPerPageFromStorage) == null ? $scope.itemsPerPageOptions[1] : Storage.get(itemsPerPageFromStorage);
                $scope.$watch('itemsPerPage', function(newValue, oldValue) {
                    newValue = newValue == null ? $scope.itemsPerPageOptions[1] : newValue;
                    Storage.set(itemsPerPageFromStorage, newValue);
                    $scope.update();
                });

                // searchText setting
                var searchTextFromStorage = $scope.properties.type + '-searchText';
                $scope.searchText = Storage.get(searchTextFromStorage) == null ? "" : Storage.get(searchTextFromStorage);
                $scope.$watch('searchText', function(newValue, oldValue) {
                    newValue = newValue == null ? "" : newValue;
                    Storage.set(searchTextFromStorage, newValue);
                });

                // pagination page setting
                var currentPageFromStorage = $scope.properties.type + '-currentPage';
                $scope.currentPage = Storage.get(currentPageFromStorage) == null ? 1 : Storage.get(currentPageFromStorage);
                $scope.$watch('currentPage', function (newValue, oldValue) {
                    newValue = newValue == null ? 1 : newValue;
                    Storage.set(currentPageFromStorage, newValue);
                    $scope.update();
                });

                // sort setting
                $scope.sortKey = 'modifiedDate';
                $scope.reverse = true;
                $scope.sort = function (key) {
                    $scope.sortKey = key;
                    $scope.reverse = !$scope.reverse;
                };

                //enableAlert, isDisabled & delete setting
                $scope.deleteItem = function(item) {
                    $scope.delete()(item);
                };
                $scope.isDisabled = function(item) {
                    if ($scope.properties.type == 'dashboards') {
                        $scope.disabled()(item);
                    }
                    // do nothing if its alert
                };
                $scope.enableItem = function(item, enabled) {
                    $scope.enable()(item, enabled);
                };

                // total number setting
                $scope.$watch('dataSet.length', function () {
                    $scope.update();
                });
                $scope.update = function(){
                    $scope.start = ($scope.currentPage - 1)* $scope.itemsPerPage + 1;
                    var end = $scope.start + $scope.itemsPerPage - 1;
                    $scope.end = end < $scope.dataSet.length ? end : $scope.dataSet.length;
                };
            }]
        };
    });